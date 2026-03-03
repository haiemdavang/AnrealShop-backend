package com.haiemdavang.AnrealShop.service.serviceImp;

import com.haiemdavang.AnrealShop.dto.order.OrderItemReviewProjection;
import com.haiemdavang.AnrealShop.dto.product.ProductMediaDto;
import com.haiemdavang.AnrealShop.dto.product.ProductReviewDto;
import com.haiemdavang.AnrealShop.dto.review.CreateReviewRequest;
import com.haiemdavang.AnrealShop.dto.review.ReviewListResponse;
import com.haiemdavang.AnrealShop.exception.BadRequestException;
import com.haiemdavang.AnrealShop.mapper.PreviewMapper;
import com.haiemdavang.AnrealShop.mapper.ProductMapper;
import com.haiemdavang.AnrealShop.modal.entity.order.OrderItem;
import com.haiemdavang.AnrealShop.modal.entity.product.Product;
import com.haiemdavang.AnrealShop.modal.entity.product.ProductReview;
import com.haiemdavang.AnrealShop.modal.entity.product.ProductReviewMedia;
import com.haiemdavang.AnrealShop.modal.entity.user.User;
import com.haiemdavang.AnrealShop.modal.enums.MediaType;
import com.haiemdavang.AnrealShop.modal.enums.OrderTrackStatus;
import com.haiemdavang.AnrealShop.modal.entity.shop.ShopOrder;
import com.haiemdavang.AnrealShop.modal.enums.ShopOrderStatus;
import com.haiemdavang.AnrealShop.repository.order.OrderItemRepository;
import com.haiemdavang.AnrealShop.repository.order.ShopOrderRepository;
import com.haiemdavang.AnrealShop.repository.product.ProductRepository;
import com.haiemdavang.AnrealShop.repository.product.ProductReviewRepository;
import com.haiemdavang.AnrealShop.security.SecurityUtils;
import com.haiemdavang.AnrealShop.service.IReviewService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImp implements IReviewService {

    private final ProductReviewRepository productReviewRepository;
    private final OrderItemRepository orderItemRepository;
    private final ShopOrderRepository shopOrderRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final PreviewMapper previewMapper;
    private final SecurityUtils securityUtils;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public void createReview(CreateReviewRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        // 1. Tìm OrderItem qua projection (chỉ lấy các trường cần thiết)
        OrderItemReviewProjection projection = orderItemRepository.findWithOrderAndProductById(request.getOrderItemId())
                .orElseThrow(() -> new BadRequestException("ORDER_ITEM_NOT_FOUND"));

        // 2. Kiểm tra OrderItem có thuộc về user hiện tại không
        if (!projection.getUserId().equals(currentUser.getId())) {
            throw new BadRequestException("ORDER_ITEM_NOT_BELONG_TO_USER");
        }

        // 3. Kiểm tra đơn hàng đã giao (DELIVERED) chưa
        if (projection.getStatus() != OrderTrackStatus.DELIVERED) {
            throw new BadRequestException("ORDER_ITEM_NOT_DELIVERED");
        }

        // 4. Kiểm tra OrderItem đã được review chưa
        if (productReviewRepository.existsByOrderItemId(request.getOrderItemId())) {
            throw new BadRequestException("ORDER_ITEM_ALREADY_REVIEWED");
        }

        // 5. Lấy reference entities (không query DB, chỉ tạo proxy)
        Product product = entityManager.getReference(Product.class, projection.getProductId());
        OrderItem orderItemRef = entityManager.getReference(OrderItem.class, projection.getOrderItemId());

        // 6. Tạo ProductReview
        ProductReview review = ProductReview.builder()
                .user(currentUser)
                .product(product)
                .orderItem(orderItemRef)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        // 7. Thêm media nếu có
        if (request.getMediaList() != null && !request.getMediaList().isEmpty()) {
            for (ProductMediaDto mediaDto : request.getMediaList()) {
                ProductReviewMedia media = ProductReviewMedia.builder()
                        .mediaUrl(mediaDto.getUrl())
                        .mediaType(mediaDto.getType() != null ? MediaType.valueOf(mediaDto.getType()) : MediaType.IMAGE)
                        .build();
                review.addMedia(media);
            }
        }

        // 8. Lưu review
        productReviewRepository.save(review);

        orderItemRef.setStatus(OrderTrackStatus.SUCCESS);
        orderItemRepository.save(orderItemRef);

        // 9. Cập nhật averageRating và totalReviews cho product
        updateProductRating(product);

        // 10. Kiểm tra tất cả order items (DELIVERED) của shop order đã được review chưa → cập nhật SUCCESS
        ShopOrder shopOrderRef = entityManager.getReference(ShopOrder.class, projection.getShopOrderId());
        this.checkAndUpdateShopOrderStatusSuccess(shopOrderRef);
    }

    @Override
    public ReviewListResponse getReviewsByProductId(String productId, int size) {
        Set<ProductReview> reviews = productReviewRepository.findByProductId(productId);

        // Summary tính trên toàn bộ reviews
        ReviewListResponse.ReviewListResponseBuilder builder = ReviewListResponse.builder()
                .summary(previewMapper.buildReviewSummary(reviews));

        // Danh sách reviews giới hạn theo size
        List<ProductReviewDto> reviewDtos = reviews.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(size)
                .map(productMapper::toProductReviewDto)
                .toList();

        return builder.reviews(reviewDtos).build();
    }

    @Override
    public Set<String> getReviewedOrderItemIds(Collection<String> orderItemIds) {
        if (orderItemIds == null || orderItemIds.isEmpty()) {
            return Set.of();
        }
        return productReviewRepository.findReviewedOrderItemIds(orderItemIds);
    }

    private void updateProductRating(Product product) {
        Set<ProductReview> allReviews = productReviewRepository.findByProductId(product.getId());
        int totalReviews = allReviews.size();
        float averageRating = 0;
        if (totalReviews > 0) {
            averageRating = (float) allReviews.stream()
                    .mapToInt(ProductReview::getRating)
                    .sum() / totalReviews;
        }
        product.setAverageRating(averageRating);
        product.setTotalReviews(totalReviews);
        productRepository.save(product);
    }

    private void checkAndUpdateShopOrderStatusSuccess(ShopOrder shopOrder) {
        ShopOrder fullShopOrder = shopOrderRepository.findWithOrderItemById(shopOrder.getId());

        if (fullShopOrder.getStatus() != ShopOrderStatus.DELIVERED) {
            return;
        }

        Set<String> deliveredOrderItemIds = fullShopOrder.getOrderItems().stream()
                .filter(item -> item.getStatus() == OrderTrackStatus.SUCCESS)
                .map(OrderItem::getId)
                .collect(Collectors.toSet());

        if (deliveredOrderItemIds.isEmpty()) {
            return;
        }

        // Kiểm tra tất cả order items DELIVERED đều đã có review
        Set<String> reviewedIds = productReviewRepository.findReviewedOrderItemIds(deliveredOrderItemIds);

        if (reviewedIds.containsAll(deliveredOrderItemIds)) {
            fullShopOrder.setStatus(ShopOrderStatus.SUCCESS);
            shopOrderRepository.save(fullShopOrder);
            log.info("ShopOrder {} đã được cập nhật thành SUCCESS vì tất cả order items đã được đánh giá", fullShopOrder.getId());
        }
    }


}
