package com.haiemdavang.AnrealShop.mapper;

import com.haiemdavang.AnrealShop.dto.attribute.ProductAttributeDto;
import com.haiemdavang.AnrealShop.dto.attribute.ProductAttributeSingleValueDto;
import com.haiemdavang.AnrealShop.dto.product.*;
import com.haiemdavang.AnrealShop.tech.elasticsearch.document.EsCategory;
import com.haiemdavang.AnrealShop.tech.elasticsearch.document.EsProduct;
import com.haiemdavang.AnrealShop.modal.entity.category.Category;
import com.haiemdavang.AnrealShop.modal.entity.product.Product;
import com.haiemdavang.AnrealShop.modal.entity.product.ProductReview;
import com.haiemdavang.AnrealShop.modal.entity.product.ProductSku;
import com.haiemdavang.AnrealShop.modal.entity.shop.Shop;
import com.haiemdavang.AnrealShop.utils.ApplicationInitHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductMapper {
    private final ShopMapper shopMapper;
    private final CategoryMapper categoryMapper;
    private final AttributeMapper attributeMapper;

    public Product toEntity(BaseProductRequest baseProductRequest, Category category, Shop shop) {
        Product product = Product.builder()
                .name(baseProductRequest.getName())
                .urlSlug(ApplicationInitHelper.toSlug(baseProductRequest.getName()))
                .description(baseProductRequest.getDescription())
                .sortDescription(baseProductRequest.getSortDescription())
                .price(baseProductRequest.getPrice())
                .discountPrice(baseProductRequest.getDiscountPrice())
                .quantity(baseProductRequest.getQuantity())
                .weight(baseProductRequest.getWeight())
                .height(baseProductRequest.getHeight())
                .length(baseProductRequest.getLength())
                .width(baseProductRequest.getWidth())
                .build();
        if (shop != null) {
            product.setShop(shop);
        }
        if (category != null) {
            product.setCategory(category);
        }
        return product;
    }

    public void updateEntity(Product product, BaseProductRequest baseProductRequest, Category category) {
        if (product == null || baseProductRequest == null) {
            return;
        }
        product.setName(baseProductRequest.getName());
        product.setUrlSlug(ApplicationInitHelper.toSlug(baseProductRequest.getName()));
        product.setDescription(baseProductRequest.getDescription());
        product.setSortDescription(baseProductRequest.getSortDescription());
        product.setPrice(baseProductRequest.getPrice());
        product.setDiscountPrice(baseProductRequest.getDiscountPrice());
        product.setQuantity(baseProductRequest.getQuantity());
        product.setWeight(baseProductRequest.getWeight());
        product.setHeight(baseProductRequest.getHeight());
        product.setLength(baseProductRequest.getLength());
        product.setWidth(baseProductRequest.getWidth());

        if (category != null) {
            product.setCategory(category);
        }
    }

    public ProductSku toSkuEntity(BaseProductSkuRequest skuRequest, Product product) {
        if (skuRequest == null) {
            return null;
        }

        return ProductSku.builder()
                .sku(skuRequest.getSku())
                .price(skuRequest.getPrice())
                .quantity(skuRequest.getQuantity())
                .thumbnailUrl(skuRequest.getImageUrl())
                .product(product)
                .attributes(new HashSet<>())
                .build();
    }

    public ProductDetailDto toProductDetailDto(Product product, List<ProductAttributeSingleValueDto> attributeValues) {
        if (product == null) {
            return null;
        }

        List<ProductAttributeDto> attributes = new ArrayList<>();
        if (attributeValues != null && !attributeValues.isEmpty()) {
            attributes = attributeMapper.toProductAttributeDto(attributeValues);
        }

        List<ProductMediaDto> mediaList = product.getMediaList() != null ?
                product.getMediaList().stream()
                        .map(media -> ProductMediaDto.builder()
                                .id(media.getId())
                                .url(media.getUrl())
                                .thumbnailUrl(media.getUrl())
                                .type(media.getType().name())
                                .build())
                        .toList() :
                new ArrayList<>();

        return ProductDetailDto.builder()
                .id(product.getId())
                .name(product.getName())
                .thumbnailUrl(product.getThumbnailUrl())
                .urlSlug(product.getUrlSlug())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryPath(product.getCategory() != null ? product.getCategory().getUrlPath() : null)
                .description(product.getDescription())
                .sortDescription(product.getSortDescription())
                .price(product.getPrice())
                .discountPrice(product.getDiscountPrice())
                .quantity(product.getQuantity())
                .sold(product.getSold())
                .status(product.getRestrictStatus() != null ? product.getRestrictStatus().getId() : null)
                .visible(product.isVisible())
                .createdAt(product.getCreatedAt() != null ? product.getCreatedAt().toString() : null)
                .updatedAt(product.getUpdatedAt() != null ? product.getUpdatedAt().toString() : null)
                .restrictedReason(product.getRestrictedReason())
                .isRestricted(product.isRestricted())
                .restrictStatus(product.getRestrictStatus() != null ? product.getRestrictStatus().getId() : null)
                .averageRating(product.getAverageRating())
                .totalReviews(product.getTotalReviews())
                .weight(product.getWeight())
                .width(product.getWidth())
                .height(product.getHeight())
                .length(product.getLength())
                .baseShopDto(shopMapper.toBaseShopDto(product.getShop()))
                .medias(mediaList)
                .attributes(attributes)
                .build();
    }


    // EsproductDto
    public EsProductDto toEsProductDto(Product product, List<ProductAttributeDto> attributeValues) {
        if (product == null) {
            return null;
        }


        return EsProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .sortDescription(product.getSortDescription())
                .slug(product.getUrlSlug())
                .thumbnailUrl(product.getThumbnailUrl())
                .price(product.getPrice())
                .discountPrice(product.getDiscountPrice())
                .quantity(product.getQuantity())
                .averageRating(product.getAverageRating())
                .totalReviews(product.getTotalReviews())
                .revenue(product.getRevenue())
                .sold(product.getSold())
                .visible(product.isVisible())
                .restrictStatus(product.getRestrictStatus() != null ? product.getRestrictStatus().getId() : null)
                .createdAt(product.getCreatedAt() == null ? LocalDateTime.now().toString() : product.getCreatedAt().toString())
                .updatedAt(product.getUpdatedAt() == null ? LocalDateTime.now().toString() : product.getUpdatedAt().toString())
                .shop(shopMapper.toBaseShopDto(product.getShop()))
                .category(categoryMapper.toBaseCategoryDto(product.getCategory()))
                .attributes(attributeValues)
                .build();
    }
    public EsProductDto toEsProductDto(EsProduct esProduct, EsCategory esCategory) {
        if (esProduct == null) {
            return null;
        }

        return EsProductDto.builder()
                .id(esProduct.getId())
                .name(esProduct.getName())
                .sortDescription(esProduct.getSortDescription())
                .description(esProduct.getDescription())
                .slug(esProduct.getUrlSlug())
                .thumbnailUrl(esProduct.getThumbnailUrl())
                .price(esProduct.getPrice())
                .discountPrice(esProduct.getDiscountPrice())
                .quantity(esProduct.getQuantity())
                .averageRating(esProduct.getAverageRating())
                .totalReviews(esProduct.getTotalReviews())
                .revenue(esProduct.getRevenue())
                .sold(esProduct.getSold())
                .visible(esProduct.getVisible())
                .restrictStatus(esProduct.getRestrictStatus())
                .createdAt(LocalDateTime.ofInstant(esProduct.getCreatedAt(), ZoneOffset.systemDefault()).toString())
                .updatedAt(LocalDateTime.ofInstant(esProduct.getUpdatedAt(), ZoneOffset.systemDefault()).toString())
                .shop(shopMapper.toBaseShopDto(esProduct.getShop()))
                .category(categoryMapper.toBaseCategoryDto(esCategory))
                .attributes(attributeMapper.toProductAttributeDtoFromEs(esProduct.getAttributes()))
                .build();
    }
    public EsProduct toEsProduct(EsProductDto esProductDto) {
        if (esProductDto == null) {
            return null;
        }

        return EsProduct.builder()
                .id(esProductDto.getId())
                .name(esProductDto.getName())
                .sortDescription(esProductDto.getSortDescription())
                .description(esProductDto.getDescription())
                .suggest(esProductDto.getName())
                .urlSlug(esProductDto.getSlug())
                .price(esProductDto.getPrice())
                .discountPrice(esProductDto.getDiscountPrice())
                .quantity(esProductDto.getQuantity())
                .thumbnailUrl(esProductDto.getThumbnailUrl())
                .restrictStatus(esProductDto.getRestrictStatus())
                .createdAt(LocalDateTime.parse(esProductDto.getCreatedAt()).atZone(ZoneOffset.systemDefault()).toInstant())
                .updatedAt(LocalDateTime.parse(esProductDto.getUpdatedAt()).atZone(ZoneOffset.systemDefault()).toInstant())
                .sold(esProductDto.getSold())
                .revenue(esProductDto.getRevenue())
                .averageRating(esProductDto.getAverageRating())
                .totalReviews(esProductDto.getTotalReviews())
                .visible(esProductDto.isVisible())
                .shop(shopMapper.toEsShop(esProductDto.getShop()))
                .categoryId(esProductDto.getCategory() != null ? esProductDto.getCategory().getId() : null)
                .attributes(attributeMapper.toEsAttributes(esProductDto.getAttributes()))
                .build();
    }

    // myshop product mânager
    public MyShopProductSkuDto toMyShopProductSkuDto(ProductSku productSku) {
        if (productSku == null) {
            return null;
        }
        return MyShopProductSkuDto.builder()
                .id(productSku.getId())
                .sku(productSku.getSku())
                .imageUrl(productSku.getThumbnailUrl())
                .price(productSku.getPrice())
                .quantity(productSku.getQuantity())
                .sold(productSku.getSold())
                .createdAt(productSku.getCreatedAt() != null ? productSku.getCreatedAt().toString() : null)
                .keyAttributes(productSku.getAttributes().stream().map(t -> t.getAttributeKey().getKeyName()).toArray(String[]::new))
                .attributeForSku(attributeMapper.toProductAttributeSingleValueDto(productSku.getAttributes()))
                .build();
    }
    public MyShopProductDto toMyShopProductDto(Product product, Set<ProductSku> productSkus) {
        if (product == null) {
            return null;
        }

        List<MyShopProductSkuDto> skuDtos = new ArrayList<>();
        if (productSkus != null && !productSkus.isEmpty()) {
            skuDtos = productSkus.stream()
                    .map(this::toMyShopProductSkuDto)
                    .toList();
        }
        return MyShopProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .thumbnailUrl(product.getThumbnailUrl())
                .urlSlug(product.getUrlSlug())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryPath(product.getCategory() != null ? product.getCategory().getUrlPath() : null)
                .discountPrice(product.getDiscountPrice())
                .quantity(product.getQuantity())
                .sold(product.getSold())
                .status(product.getRestrictStatus() != null ? product.getRestrictStatus().getId() : null)
                .visible(product.isVisible())
                .createdAt(product.getCreatedAt() != null ? product.getCreatedAt().toString() : null)
                .productSkus(skuDtos)
                .build();
    }

    public BaseProductSkuRequest toBaseProductSkuRequest(ProductSku productSku) {
        if (productSku == null) {
            return null;
        }

        List<ProductAttributeDto> attributes = attributeMapper.toProductAttributeDtoFromAttributeValue(productSku.getAttributes().stream().toList());
        attributes = attributeMapper.formatAttributes(attributes);


        return BaseProductSkuRequest.builder()
                .sku(productSku.getSku())
                .price(productSku.getPrice())
                .quantity(productSku.getQuantity())
                .imageUrl(productSku.getThumbnailUrl())
                .attributes(attributes)
                .build();
    }
    public BaseProductRequest toBaseProductRequest(Product product, List<ProductSku> skuForProduct, List<ProductAttributeSingleValueDto> attributeValues) {
        if (product == null) {
            return null;
        }

        List<BaseProductSkuRequest> skuRequests = new ArrayList<>();
        if (skuForProduct != null && !skuForProduct.isEmpty()) {
            skuRequests = skuForProduct.stream()
                    .map(this::toBaseProductSkuRequest)
                    .toList();
        }

        List<ProductAttributeDto> productAttributes = new ArrayList<>();
        if (attributeValues != null && !attributeValues.isEmpty()) {
            productAttributes = attributeMapper.toProductAttributeDto(attributeValues.stream().toList());
        }

        List<ProductMediaDto> mediaList = product.getMediaList() != null ?
                product.getMediaList().stream()
                        .map(media -> ProductMediaDto.builder()
                                .id(media.getId())
                                .url(media.getUrl())
                                .thumbnailUrl(media.getUrl())
                                .type(media.getType().name())
                                .build())
                        .toList() :
                new ArrayList<>();

        return BaseProductRequest.builder()
                .name(product.getName())
                .description(product.getDescription())
                .sortDescription(product.getSortDescription())
                .price(product.getPrice())
                .discountPrice(product.getDiscountPrice())
                .quantity(product.getQuantity())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryPath(product.getCategory() != null ? product.getCategory().getUrlPath() : null)
                .weight(product.getWeight())
                .height(product.getHeight())
                .length(product.getLength())
                .width(product.getWidth())
                .productSkus(skuRequests)
                .attributes(productAttributes)
                .media(mediaList)
                .build();
    }

    //  admin product manager
    public MyShopProductDto toAdminProductDto(Product product) {
        if (product == null) {
            return null;
        }
        MyShopProductDto myShopProductDto = toMyShopProductDto(product, new HashSet<>());
        myShopProductDto.setRestricted(product.isRestricted());
        myShopProductDto.setRestrictedReason(product.getRestrictedReason());
        myShopProductDto.setCategoryPath(product.getCategory().getUrlPath());
        myShopProductDto.setBaseShopDto(shopMapper.toBaseShopDto(product.getShop()));
        return myShopProductDto;
    }
    //    home
    public UserProductDto toUserProductDto(EsProductDto esProduct) {
        if (esProduct == null) {
            return null;
        }

        return UserProductDto.builder()
                .id(esProduct.getId())
                .name(esProduct.getName())
                .thumbnailUrl(esProduct.getThumbnailUrl())
                .sortDescription(esProduct.getSortDescription())
                .urlSlug(esProduct.getSlug())
                .discountPrice(esProduct.getDiscountPrice())
                .price(esProduct.getPrice())
                .quantity(esProduct.getQuantity())
                .sold(esProduct.getSold())
                .averageRating(esProduct.getAverageRating())
                .totalReviews(esProduct.getTotalReviews())
                .categoryId(esProduct.getCategory().getId())
                .categoryName(esProduct.getCategory().getName())
                .shopId(esProduct.getShop() != null ? esProduct.getShop().getId() : null)
                .shopName(esProduct.getShop() != null ? esProduct.getShop().getName() : null)
                .shopThumbnailUrl(esProduct.getThumbnailUrl())
                .build();
    }
    public List<UserProductDto> toUserProductDtos(List<EsProductDto> esProducts) {
        if (esProducts == null || esProducts.isEmpty()) {
            return new ArrayList<>();
        }

        return esProducts.stream()
                .map(this::toUserProductDto)
                .toList();
    }

    //  product review
    public ProductReviewDto toProductReviewDto(ProductReview productReview) {
        if (productReview == null) {
            return null;
        }

        List<ProductMediaDto> mediaList = new ArrayList<>();
        if (productReview.getMediaList() != null && !productReview.getMediaList().isEmpty()) {
            mediaList = productReview.getMediaList().stream()
                    .map(media -> ProductMediaDto.builder()
                            .id(media.getId())
                            .url(media.getMediaUrl())
                            .thumbnailUrl(media.getMediaUrl())
                            .type(media.getMediaType().name())
                            .build())
                    .toList();
        }

        return ProductReviewDto.builder()
                .id(productReview.getId())
                .userId(productReview.getUser() != null ? productReview.getUser().getId() : null)
                .userName(productReview.getUser() != null ? productReview.getUser().getFullName() : null)
                .userAvatarUrl(productReview.getUser() != null ? productReview.getUser().getAvatarUrl() : null)
                .orderItemId(productReview.getOrderItem() != null ? productReview.getOrderItem().getId() : null)
                .rating(productReview.getRating())
                .comment(productReview.getComment())
                .createdAt(productReview.getCreatedAt())
                .mediaList(mediaList)
                .build();
    }
    public ProductReviewDto toProductReviewDtoFull(ProductReview productReview) {
        ProductReviewDto productReviewDto = toProductReviewDto(productReview);
        productReviewDto.setProductId(productReview.getProduct() != null ? productReview.getProduct().getId() : null);
        productReviewDto.setProductName(productReview.getProduct() != null ? productReview.getProduct().getName() : null);
        productReviewDto.setUpdatedAt(productReview.getUpdatedAt());

        return productReviewDto;
    }
}
