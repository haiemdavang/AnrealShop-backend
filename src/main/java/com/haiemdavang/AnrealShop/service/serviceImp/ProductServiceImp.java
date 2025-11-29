package com.haiemdavang.AnrealShop.service.serviceImp;

import com.haiemdavang.AnrealShop.dto.attribute.ProductAttributeDto;
import com.haiemdavang.AnrealShop.dto.attribute.ProductAttributeSingleValueDto;
import com.haiemdavang.AnrealShop.dto.SortEnum;
import com.haiemdavang.AnrealShop.dto.product.*;
import com.haiemdavang.AnrealShop.tech.elasticsearch.document.EsProduct;
import com.haiemdavang.AnrealShop.tech.elasticsearch.service.ProductIndexerService;
import com.haiemdavang.AnrealShop.exception.BadRequestException;
import com.haiemdavang.AnrealShop.tech.kafka.dto.ProductSyncActionType;
import com.haiemdavang.AnrealShop.tech.kafka.dto.ProductSyncMessage;
import com.haiemdavang.AnrealShop.tech.kafka.producer.ProductKafkaProducer;
import com.haiemdavang.AnrealShop.mapper.AttributeMapper;
import com.haiemdavang.AnrealShop.mapper.ProductMapper;
import com.haiemdavang.AnrealShop.modal.entity.attribute.AttributeKey;
import com.haiemdavang.AnrealShop.modal.entity.attribute.AttributeValue;
import com.haiemdavang.AnrealShop.modal.entity.category.Category;
import com.haiemdavang.AnrealShop.modal.entity.order.OrderItem;
import com.haiemdavang.AnrealShop.modal.entity.product.Product;
import com.haiemdavang.AnrealShop.modal.entity.product.ProductGeneralAttribute;
import com.haiemdavang.AnrealShop.modal.entity.product.ProductMedia;
import com.haiemdavang.AnrealShop.modal.entity.product.ProductSku;
import com.haiemdavang.AnrealShop.modal.entity.shop.Shop;
import com.haiemdavang.AnrealShop.modal.enums.MediaType;
import com.haiemdavang.AnrealShop.modal.enums.RestrictStatus;
import com.haiemdavang.AnrealShop.repository.AttributeKeyRepository;
import com.haiemdavang.AnrealShop.repository.AttributeValueRepository;
import com.haiemdavang.AnrealShop.repository.product.ProductGeneralAttributeRepository;
import com.haiemdavang.AnrealShop.repository.product.ProductRepository;
import com.haiemdavang.AnrealShop.repository.product.ProductSkuRepository;
import com.haiemdavang.AnrealShop.repository.product.ProductSpecification;
import com.haiemdavang.AnrealShop.security.SecurityUtils;
import com.haiemdavang.AnrealShop.service.ICategoryService;
import com.haiemdavang.AnrealShop.service.IProductService;
import com.haiemdavang.AnrealShop.utils.ApplicationInitHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImp implements IProductService {
    private final ProductRepository productRepository;
    private final ICategoryService categoryService;
    private final ProductSkuRepository productSkuRepository;
    private final ProductGeneralAttributeRepository productGeneralAttributeRepository;
    private final AttributeValueRepository attributeValueRepository;
    private final AttributeKeyRepository attributeKeyRepository;
    private final ProductMapper productMapper;

    private final ProductKafkaProducer productKafkaProducer;
    private final ProductIndexerService productIndexerService;

    private final SecurityUtils securityUtils;

    private final AttributeMapper attributeMapper;
    private final AttributeServiceImp attributeServiceImp;

    @Override
    public BaseProductRequest getMyShopProductById(String id) {
        if (!productRepository.existsById(id))  {
            throw new BadRequestException("PRODUCT_NOT_FOUND");
        }else {
            Product product = productRepository.findBaseInfoById(id);
            List<ProductSku> skuForProduct = productSkuRepository.findWithAttributeByProductIdOrProductSlug(id);
            List<ProductAttributeSingleValueDto> attributeValues = productGeneralAttributeRepository.findProductAttributeSingleValueDtoByProductId(id);

            return productMapper.toBaseProductRequest(product, skuForProduct, attributeValues);
        }
    }

    @Override
    public MyShopProductListResponse getMyShopProductsForAdmin(int page, int limit, String status, String search, LocalDate startDate, LocalDate endDate) {

        RestrictStatus restrictStatus = null;
        if (status != null && !status.isEmpty()) {
            try {
                restrictStatus = RestrictStatus.valueOf(status);
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("RESTRICT_STATUS_INVALID");
            }
        }
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime enDateTime = endDate != null ? endDate.atTime(23, 59, 59) : null;

        Specification<Product> spec = ProductSpecification.adminFilter(search, restrictStatus, startDateTime, enDateTime);
        Pageable pageable = PageRequest.of(page, limit, SortEnum.CREATED_AT_ASC.getSort());

        Page<Product> productPage = productRepository.findAll(spec, pageable);

        return MyShopProductListResponse.builder()
                .products(productPage.getContent().stream().map(productMapper::toAdminProductDto).toList())
                .currentPage(productPage.getPageable().getPageNumber() + 1)
                .totalPages(productPage.getTotalPages())
                .totalCount(productPage.getTotalElements())
                .build();
    }

    @Override
    @Transactional
    public void rejectProduct(String id, String reason) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("PRODUCT_NOT_FOUND"));

        product.setRestricted(true);
        product.setRestrictedReason(reason);
        product.setRestrictStatus(RestrictStatus.VIOLATION);
        product.setVisible(false);

        productRepository.save(product);

        ProductSyncMessage message = ProductSyncMessage.builder()
                .action(ProductSyncActionType.PRODUCT_UPDATED_STATUS)
                .isVisible(false)
                .status(RestrictStatus.VIOLATION)
                .id(id)
                .build();
        productKafkaProducer.sendProductSyncMessage(message);
    }

    @Override
    @Transactional
    public void approveProduct(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("PRODUCT_NOT_FOUND"));

        product.setRestricted(false);
        product.setRestrictedReason("Xac nhan san pham thanh cong");
        if (product.getRestrictStatus() == RestrictStatus.PENDING || product.getRestrictStatus() == RestrictStatus.VIOLATION) {
            product.setRestrictStatus(RestrictStatus.ACTIVE);
            product.setVisible(true);
        }
        productRepository.save(product);

        ProductSyncMessage message = ProductSyncMessage.builder()
                .action(ProductSyncActionType.PRODUCT_UPDATED_STATUS)
                .isVisible(true)
                .status(product.getRestrictStatus())
                .id(id)
                .build();
        productKafkaProducer.sendProductSyncMessage(message);
    }

    @Override
    public List<ProductStatusDto> getFilterMetaForAdmin(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime enDateTime = endDate != null ? endDate.atTime(23, 59, 59) : null;

        Set<IProductStatus> dataResult = productRepository.getMetaSumByStatusForAdmin(startDateTime, enDateTime);
        return convertToProductStatusDto(dataResult, RestrictStatus.getOrderForAdmin());
    }

    @Override
    public ProductDetailDto getProductById(String id, boolean isReview) {
//        isReview chua trien khai nghe haidev
        Product product = productRepository.findFullInfoByIdOrSlug(id)
                .orElseThrow(() -> new BadRequestException("PRODUCT_NOT_FOUND"));
        List<ProductAttributeSingleValueDto> productAttributes = productGeneralAttributeRepository.findProductAttributeSingleValueDtoByProductId(id);

        ProductDetailDto response = productMapper.toProductDetailDto(product, productAttributes);

        List<ProductSku> productSkus = productSkuRepository.findWithAttributeByProductIdOrProductSlug(id);
        List<MyShopProductSkuDto> skuDtos = new ArrayList<>();
        if (productSkus != null && !productSkus.isEmpty()) {
            skuDtos = product.getProductSkus().stream()
                    .map(productMapper::toMyShopProductSkuDto)
                    .toList();
        }
        response.setProductSkus(skuDtos);
        return response;
    }

    @Override
    @Transactional
    public void createProduct(BaseProductRequest baseProductRequest) {
        Shop currentUserShop = securityUtils.getCurrentUserShop();

        Category category = categoryService.findByIdAndThrow(baseProductRequest.getCategoryId());

        Product product = productMapper.toEntity(baseProductRequest, category, currentUserShop);

        this.updateProductMediaList(baseProductRequest.getMedia(), product);

        List<ProductAttributeDto> allAttributes = new ArrayList<>();


        if (baseProductRequest.getAttributes() != null && !baseProductRequest.getAttributes().isEmpty()) {
            Set<ProductAttributeDto> requestedGeneralAttributes = new HashSet<>(baseProductRequest.getAttributes());
            allAttributes.addAll(requestedGeneralAttributes);
            processProductAttributes(product, requestedGeneralAttributes);
        }
        Product newProduct = productRepository.save(product);

        List<ProductSku> productSkus = new ArrayList<>();
        if (baseProductRequest.getProductSkus() != null && !baseProductRequest.getProductSkus().isEmpty()) {
            Set<ProductAttributeDto> allSkuAttributes = new HashSet<>();
            for (BaseProductSkuRequest skuRequest : baseProductRequest.getProductSkus()) {
                ProductSku productSku = productMapper.toSkuEntity(skuRequest, newProduct);

                if (skuRequest.getAttributes() != null && !skuRequest.getAttributes().isEmpty()) {
                    Set<ProductAttributeDto> requestedSkuAttributes = new HashSet<>(skuRequest.getAttributes());
                    allSkuAttributes.addAll(requestedSkuAttributes);
                    processSkuAttributes(productSku, requestedSkuAttributes);
                }
                productSkus.add(productSku);
            }
            Map<String, ProductAttributeDto> skuAttributesMap = allSkuAttributes.stream()
                    .collect(Collectors.toMap(ProductAttributeDto::getAttributeKeyName,
                            attr -> ProductAttributeDto.builder()
                                    .attributeKeyName(attr.getAttributeKeyName())
                                    .attributeKeyDisplay(attr.getAttributeKeyDisplay())
                                    .values(new ArrayList<>(attr.getValues()))
                                    .build(), (oldA, newA) -> {
                                oldA.getValues().addAll(newA.getValues());
                                return oldA;
                            }));
            allAttributes.addAll(skuAttributesMap.values());
        }
        if(!productSkus.isEmpty())
            productSkuRepository.saveAll(productSkus);

        EsProductDto esProductDto = productMapper.toEsProductDto(newProduct, allAttributes);
        ProductSyncMessage message = ProductSyncMessage.builder()
                .action(ProductSyncActionType.CREATE)
                .product(esProductDto).build();
        productKafkaProducer.sendProductSyncMessage(message);
    }

    @Override
    @Transactional
    public void updateProduct(String id, BaseProductRequest baseProductRequest) {
        Product product = productRepository.findWithCategoryAndMediaAndGeneralAttributeById(id)
                .orElseThrow(() -> new BadRequestException("PRODUCT_NOT_FOUND"));

        Category category = null;
        if (baseProductRequest.getCategoryId() != null && categoryService.existsById(baseProductRequest.getCategoryId())) {
            if (product.getCategory() != null && !product.getCategory().getId().equals(baseProductRequest.getCategoryId())) {
                category = categoryService.getReferenceById(baseProductRequest.getCategoryId());
            }
        } else {
            throw new BadRequestException("CATEGORY_NOT_FOUND");
        }
        productMapper.updateEntity(product, baseProductRequest, category);

        this.updateProductMediaList(baseProductRequest.getMedia(), product);

        List<ProductGeneralAttribute> oldAttributeForProduct = productGeneralAttributeRepository.findProductGeneralAttributesByProductId(id);

        this.updateAttributeForProduct(oldAttributeForProduct, baseProductRequest.getAttributes(), product);

        List<ProductSku> oldProductSkus = productSkuRepository.findWithAttributeByProductIdOrProductSlug(id);

        List<ProductAttributeDto> attributeList = new ArrayList<>();
        if (baseProductRequest.getAttributes() != null) {
            attributeList.addAll(baseProductRequest.getAttributes());
        }
        this.updateProductSkus(oldProductSkus, baseProductRequest.getProductSkus(), product);
        for (BaseProductSkuRequest sku : baseProductRequest.getProductSkus()) {
            if (sku.getAttributes() != null && !sku.getAttributes().isEmpty()) {
                attributeList.addAll(sku.getAttributes());
            }
        }

        productRepository.save(product);

        ProductSyncMessage message = ProductSyncMessage.builder()
                .action(ProductSyncActionType.UPDATE)
                .product(productMapper.toEsProductDto(product, attributeMapper.formatAttributes(attributeList)))
                .build();
        productKafkaProducer.sendProductSyncMessage(message);
    }

    @Override
    @Transactional
    public void updateProductVisible(String id, boolean visible) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("PRODUCT_NOT_FOUND"));
        product.setVisible(visible);
        product.setRestrictStatus(visible ? RestrictStatus.ACTIVE : RestrictStatus.HIDDEN);
        productRepository.save(product);
        ProductSyncMessage message = ProductSyncMessage.builder()
                .action(ProductSyncActionType.PRODUCT_UPDATED_VISIBILITY)
                .isVisible(visible).id(id)
                .build();
        productKafkaProducer.sendProductSyncMessage(message);
    }

    @Override
    public void updateProductVisible(Set<String> ids, boolean visible) {
        Set<Product> products = productRepository.findByIdIn(ids);
        if (products.isEmpty() || products.size() != ids.size()) {
            throw new BadRequestException("PRODUCT_NOT_FOUND_IN_LIST");
        }else {
            RestrictStatus status = visible ? RestrictStatus.ACTIVE : RestrictStatus.HIDDEN;
            products.forEach(t -> {
                t.setRestrictStatus(status);
                t.setVisible(visible);
            });
            productRepository.saveAll(products);
            ProductSyncMessage message = ProductSyncMessage.builder()
                    .action(ProductSyncActionType.PRODUCT_UPDATE_MULTI_VISIBILITY)
                    .isVisible(visible).ids(ids)
                    .build();
            productKafkaProducer.sendProductSyncMessage(message);
        }
    }

    @Override
    @Transactional
    public void delete(String id, boolean isForce) {
        if(productRepository.existsById(id)){
            if (isForce) {
                productRepository.deleteById(id);
                ProductSyncMessage message = ProductSyncMessage.builder()
                        .action(ProductSyncActionType.DELETE).id(id).build();
                productKafkaProducer.sendProductSyncMessage(message);
            } else {
                productRepository.softDelById(id);
                ProductSyncMessage message = ProductSyncMessage.builder()
                        .action(ProductSyncActionType.PRODUCT_UPDATED_VISIBILITY)
                        .isVisible(false).id(id)
                        .build();
                productKafkaProducer.sendProductSyncMessage(message);
            }
        }
        else {
            throw new BadRequestException("PRODUCT_NOT_FOUND");
        }
    }

    @Override
    @Transactional
    public void delete(Set<String> ids, boolean isForce) {
        Set<Product> products = productRepository.findByIdIn(ids);
        if (products.isEmpty() || products.size() != ids.size()) {
            throw new BadRequestException("PRODUCT_NOT_FOUND_IN_LIST");
        }else {
            if(isForce) {
                productRepository.deleteByIdIn(ids);
                ProductSyncMessage message = ProductSyncMessage.builder()
                        .action(ProductSyncActionType.MULTI_DELETE).ids(ids).build();
                productKafkaProducer.sendProductSyncMessage(message);
            }else {
                productRepository.softDelById(ids);
                ProductSyncMessage message = ProductSyncMessage.builder()
                        .action(ProductSyncActionType.PRODUCT_UPDATE_MULTI_VISIBILITY)
                        .isVisible(false).ids(ids).build();
                productKafkaProducer.sendProductSyncMessage(message);
            }
        }
    }

    @Override
    public List<String> suggestMyProductsName(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return Collections.emptyList();
        }
        Shop currentUserShop = securityUtils.getCurrentUserShop();

        return productIndexerService.suggestMyProductsName(keyword, currentUserShop.getId()).stream().map(EsProduct::getName).toList();
    }

    @Override
    public List<ProductStatusDto> getFilterMeta() {
        Shop currentUserShop = securityUtils.getCurrentUserShop();
        Set<IProductStatus> dataResult = productRepository.getMetaSumMyProductByStatus(currentUserShop.getId());
        return convertToProductStatusDto(dataResult, RestrictStatus.getOrderDefault());
    }

    @Override
    public MyShopProductListResponse getMyShopProducts(int page, int limit, String status, String search, String categoryId, String sortBy) {
        Shop currentUserShop = securityUtils.getCurrentUserShop();
        Category category = null;
        if(categoryId != null && !categoryId.isEmpty()) {
            category = categoryService.findByIdOrUrlSlug(categoryId);
        }
        RestrictStatus restrictStatus = null;
        if (status != null && !status.isEmpty()) {
            try {
                restrictStatus = RestrictStatus.valueOf(status);
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("RESTRICT_STATUS_INVALID");
            }
        }

        Specification<Product> spec = ProductSpecification.myShopFilter(search, category == null ? null : category.getId(), currentUserShop.getId(), restrictStatus);
        Pageable pageable = PageRequest.of(page, limit, ApplicationInitHelper.getSortBy(sortBy));

        Page<Product> productPage = productRepository.findAll(spec, pageable);

        List<String> productIds = productPage.getContent().stream()
                .map(Product::getId)
                .collect(Collectors.toList());

        List<ProductSku> productSkus = productSkuRepository.findByProductSkuIdIn(productIds);

        return MyShopProductListResponse.builder()
                .products(productPage.getContent().stream().map(p -> productMapper.toMyShopProductDto(p, productSkus.stream().filter(ps -> ps.getProduct().equals(p)).collect(Collectors.toSet()))).toList())
                .currentPage(productPage.getPageable().getPageNumber() + 1)
                .totalPages(productPage.getTotalPages())
                .totalCount(productPage.getTotalElements())
                .build();
    }

    @Override
    public List<UserProductDto> getProducts(int page, int limit, String search, String categoryId, String sortBy,
                                            Double minPrice, Double maxPrice, int rating, List<String> brands, List<String> colors, List<String> sizes, List<String> origins, List<String> genders) {
        List<EsProductDto> esProducts = productIndexerService.searchProducts(page, limit, search, categoryId, sortBy, minPrice, maxPrice, rating, brands, colors, sizes, origins, genders);
        return productMapper.toUserProductDtos(esProducts);
    }

    @Override
    public List<ProductSku> findByProductSkuIdIn(Set<String> ids) {
        return productSkuRepository.findByProductSkuIdIn(ids);
    }

    @Override
    @Transactional
    public void decreaseProductSkuQuantity(Set<OrderItem> orderItems) {
        Map<Product, Set<ProductSku>> mapProducts = orderItems.stream()
                .collect(Collectors.groupingBy(
                        it -> it.getProductSku().getProduct(),
                        Collectors.mapping(OrderItem::getProductSku, Collectors.toSet())
                ));

        for (Product product : mapProducts.keySet()) {
            Map<String, ProductSku> existingSkusMap = product.getProductSkus().stream()
                    .collect(Collectors.toMap(ProductSku::getId, Function.identity()));

            int soldUpdate = 0;

            for (ProductSku sku : mapProducts.get(product)) {
                OrderItem orderItem = orderItems.stream()
                        .filter(oi -> oi.getProductSku().getId().equals(sku.getId()))
                        .findFirst()
                        .orElse(null);

                if (orderItem != null) {
                    ProductSku existingSku = existingSkusMap.get(sku.getId());

                    if (existingSku != null) {
                        existingSku.setQuantity(existingSku.getQuantity() - orderItem.getQuantity());
                        existingSku.setSold(existingSku.getSold() + orderItem.getQuantity());
                        soldUpdate += orderItem.getQuantity();
                    } else {
                        log.warn("ProductSku with id {} not found in product {}", sku.getId(), product.getId());
                    }
                } else {
                    log.warn("OrderItem not found for ProductSku with id {}", sku.getId());
                }
            }
            product.setSold(product.getSold() + soldUpdate);
            product.setQuantity(product.getQuantity() - soldUpdate);
        }

        productRepository.saveAll(mapProducts.keySet());

//        cap nhat them es nha cu
    }

    @Override
    public List<ProductSku> getProductSkuByIdIn(Set<String> strings) {
        return productSkuRepository.findByProductSkuIdIn(strings);
    }

    private void updateAttributeForProduct(List<ProductGeneralAttribute> oldAttributeForProduct,
                                          List<ProductAttributeDto> attributes,
                                          Product product) {

        Set<AttributeValue> newAttributeValues = attributeServiceImp.getAttributeValues(attributes);

        Set<ProductGeneralAttribute> attributeToDelete = oldAttributeForProduct.stream()
                .filter(pga -> !newAttributeValues.contains(pga.getAttributeValue()))
                .collect(Collectors.toSet());
        product.getGeneralAttributes().removeAll(attributeToDelete);

        Set<AttributeValue> oldAttributeValues = oldAttributeForProduct.stream()
                .map(ProductGeneralAttribute::getAttributeValue)
                .collect(Collectors.toSet());

        for (AttributeValue av : newAttributeValues) {
            if (oldAttributeValues.contains(av)) {
                continue;
            }
            product.addGeneralAttribute(av);
        }
    }

    private void updateProductSkus(List<ProductSku> oldProductSkus, List<BaseProductSkuRequest> productSkus, Product product) {
        Map<String, BaseProductSkuRequest> newSkuMap = productSkus.stream()
                .collect(Collectors.toMap(BaseProductSkuRequest::getSku, sku -> sku));

        Map<String, ProductSku> oldSkuMap = oldProductSkus.stream()
                .collect(Collectors.toMap(ProductSku::getSku, sku -> sku));

        Set<String> toDeleteSkus = oldSkuMap.keySet().stream()
                .filter(sku -> !newSkuMap.containsKey(sku))
                .collect(Collectors.toSet());

        product.getProductSkus().removeIf(sku -> toDeleteSkus.contains(sku.getSku()));

        for (BaseProductSkuRequest request : productSkus) {
            ProductSku existing = oldSkuMap.get(request.getSku());

            if (existing != null) {
                existing.setPrice(request.getPrice());
                existing.setQuantity(request.getQuantity());
                existing.setThumbnailUrl(request.getImageUrl());
            } else {
                ProductSku newSku = productMapper.toSkuEntity(request, product);
                Set<AttributeValue> attributes = attributeServiceImp.getAttributeValues(request.getAttributes());
                newSku.getAttributes().addAll(attributes);
                product.getProductSkus().add(newSku);
            }
        }
    }

    private void updateProductMediaList(List<ProductMediaDto> productMediaDto, Product product) {
        if (product.getMediaList() == null) {
            product.setMediaList(new HashSet<>());
        }

        Map<String, ProductMediaDto> newMediaDtoMap = productMediaDto.stream()
                .collect(Collectors.toMap(ProductMediaDto::getId,
                        media -> media,
                        (existing, replacement) -> existing,
                        LinkedHashMap::new));

        Set<ProductMedia> mediaToRemoves = product.getMediaList().stream()
                .filter(media -> !newMediaDtoMap.containsKey(media.getId()))
                .collect(Collectors.toSet());
        product.getMediaList().removeAll(mediaToRemoves);

        Map<String, ProductMedia> existingMediaMap = product.getMediaList().stream()
                .collect(Collectors.toMap(ProductMedia::getId, media -> media));

        Set<ProductMedia> mediaToAdd = new HashSet<>();
        for (Map.Entry<String, ProductMediaDto> entry : newMediaDtoMap.entrySet()) {
            String mediaId = entry.getKey();
            ProductMediaDto mediaDto = entry.getValue();

            ProductMedia existingMedia = existingMediaMap.get(mediaId);

            if (existingMedia == null) {
                ProductMedia newMedia = ProductMedia.builder()
                        .id(mediaId)
                        .url(mediaDto.getUrl())
                        .type(MediaType.valueOf(mediaDto.getType()))
                        .product(product)
                        .build();
                mediaToAdd.add(newMedia);
            }
        }
        product.getMediaList().addAll(mediaToAdd);

        productMediaDto.stream()
                .filter(media -> "IMAGE".equals(media.getType()))
                .findFirst()
                .ifPresent(media -> product.setThumbnailUrl(media.getUrl()));
    }

    private void processProductAttributes(Product product, Set<ProductAttributeDto> requestedAttributes) {
        Set<String> allRequestedAttributeKeyNames = requestedAttributes.stream()
                .map(ProductAttributeDto::getAttributeKeyName)
                .collect(Collectors.toSet());

        Map<String, AttributeKey> existingAttributeKeysMap = new HashMap<>();
        if (!allRequestedAttributeKeyNames.isEmpty()) {
            attributeKeyRepository.findByKeyNameIn(allRequestedAttributeKeyNames.stream().toList())
                    .forEach(ak -> existingAttributeKeysMap.put(ak.getKeyName(), ak));
        }

        Set<AbstractMap.SimpleEntry<String, String>> allRequestedKeyValuePairs = requestedAttributes.stream()
                .flatMap(attrDto -> attrDto.getValues().stream()
                        .map(val -> new AbstractMap.SimpleEntry<>(attrDto.getAttributeKeyName(), val)))
                .collect(Collectors.toSet());

        Map<AbstractMap.SimpleEntry<String, String>, AttributeValue> existingAttributeValuesMap = getOrCreateAttributeValues(
                allRequestedKeyValuePairs, existingAttributeKeysMap
        );

        for (ProductAttributeDto attrReq : requestedAttributes) {
            AttributeKey attributeKey = existingAttributeKeysMap.get(attrReq.getAttributeKeyName());
            if (attributeKey == null || attributeKey.isForSku()) {
                log.warn("AttributeKey '{}' ko tim thay.", attrReq.getAttributeKeyName());
                continue;
            }

            for (String value : attrReq.getValues()) {
                AbstractMap.SimpleEntry<String, String> keyValuePair = new AbstractMap.SimpleEntry<>(attrReq.getAttributeKeyName(), value); //
                AttributeValue attributeValue = existingAttributeValuesMap.get(keyValuePair);
                if (attributeValue == null) {
                    continue;
                }
                product.addGeneralAttribute(attributeValue);
            }
        }
    }

    private void processSkuAttributes(ProductSku productSku, Set<ProductAttributeDto> requestedAttributes) {
        Set<String> allRequestedAttributeKeyNames = requestedAttributes.stream()
                .map(ProductAttributeDto::getAttributeKeyName)
                .collect(Collectors.toSet());

        Map<String, AttributeKey> existingAttributeKeysMap = new HashMap<>();
        if (!allRequestedAttributeKeyNames.isEmpty()) {
            attributeKeyRepository.findByKeyNameIn(allRequestedAttributeKeyNames.stream().toList())
                    .forEach(ak -> existingAttributeKeysMap.put(ak.getKeyName(), ak));
        }

        Set<AbstractMap.SimpleEntry<String, String>> allRequestedKeyValuePairs = requestedAttributes.stream()
                .map(attrDto -> new AbstractMap.SimpleEntry<>(attrDto.getAttributeKeyName(), attrDto.getValues().get(0)))
                .collect(Collectors.toSet());

        Map<AbstractMap.SimpleEntry<String, String>, AttributeValue> existingAttributeValuesMap = getOrCreateAttributeValues(
                allRequestedKeyValuePairs, existingAttributeKeysMap
        );

        for (ProductAttributeDto attrReq : requestedAttributes) {
            AttributeKey attributeKey = existingAttributeKeysMap.get(attrReq.getAttributeKeyName());
            if (attributeKey == null || !attributeKey.isForSku()) {
                continue;
            }

            AbstractMap.SimpleEntry<String, String> keyValuePair = new AbstractMap.SimpleEntry<>(attrReq.getAttributeKeyName(), attrReq.getValues().get(0));
            AttributeValue attributeValue = existingAttributeValuesMap.get(keyValuePair);
            if (attributeValue == null) {
                continue;
            }
            productSku.getAttributes().add(attributeValue);
        }
    }

    private Map<AbstractMap.SimpleEntry<String, String>, AttributeValue> getOrCreateAttributeValues(
            Set<AbstractMap.SimpleEntry<String, String>> requestedKeyValuePairs,
            Map<String, AttributeKey> attributeKeysMap) {
        Map<AbstractMap.SimpleEntry<String, String>, AttributeValue> existingAttributeValuesMap = new HashMap<>();

        if (!requestedKeyValuePairs.isEmpty()) {
            Map<String, List<String>> valuesByAttributeKeyName = requestedKeyValuePairs.stream()
                    .collect(Collectors.groupingBy(AbstractMap.SimpleEntry::getKey, Collectors.mapping(AbstractMap.SimpleEntry::getValue, Collectors.toList())));

            for (Map.Entry<String, List<String>> entry : valuesByAttributeKeyName.entrySet()) {
                String keyName = entry.getKey();
                List<String> values = entry.getValue();
                AttributeKey attributeKey = attributeKeysMap.get(keyName);
                if (attributeKey != null) {
                    attributeValueRepository.findByAttributeKeyAndValueIn(attributeKey, values)
                            .forEach(av -> existingAttributeValuesMap.put(new AbstractMap.SimpleEntry<>(keyName, av.getValue()), av)); //
                }
            }
        }

        List<AttributeValue> newAttributeValues = new ArrayList<>();
        for (AbstractMap.SimpleEntry<String, String> keyValuePair : requestedKeyValuePairs) {
            if (!existingAttributeValuesMap.containsKey(keyValuePair)) {
                String keyName = keyValuePair.getKey();
                String value = keyValuePair.getValue();

                AttributeKey attributeKey = attributeKeysMap.get(keyName);
                if (attributeKey == null) {
                    log.warn("AttributeKey '{}' not found for value '{}'. Cannot create AttributeValue.", keyName, value);
                    continue;
                }
                AttributeValue newAv = AttributeValue.builder()
                        .attributeKey(attributeKey)
                        .value(value)
                        .build();
                newAttributeValues.add(newAv);
            }
        }
        if (!newAttributeValues.isEmpty()) {
            List<AttributeValue> savedNewAttributeValues = attributeValueRepository.saveAll(newAttributeValues);
            savedNewAttributeValues.forEach(av -> existingAttributeValuesMap.put(new AbstractMap.SimpleEntry<>(av.getAttributeKey().getDisplayName(), av.getValue()), av)); //
        }
        return existingAttributeValuesMap;
    }

    private List<ProductStatusDto> convertToProductStatusDto(Set<IProductStatus> dataResult, List<RestrictStatus> orderStatuses) {
        int totalCount = dataResult.stream().mapToInt(IProductStatus::getCount).sum();

        Map<RestrictStatus, IProductStatus> statusMap = dataResult.stream()
                .collect(Collectors.toMap(
                        dto -> RestrictStatus.valueOf(dto.getId()),
                        dto -> dto,
                        (existing, replacement) -> existing
                ));

        List<ProductStatusDto> result = new ArrayList<>();
        result.add(ProductStatusDto.builder()
                .id("ALL")
                .name("Tất cả")
                .count(totalCount)
                .order(0)
                .build());
        for (RestrictStatus status : orderStatuses) {
            IProductStatus dto = statusMap.get(status);
            if (dto != null) {
                result.add(
                        ProductStatusDto.builder()
                                .id(dto.getId())
                                .name(status.getName())
                                .count(dto.getCount())
                                .build()
                );
            } else {
                result.add(
                        ProductStatusDto.builder()
                                .id(status.getId())
                                .name(status.getName())
                                .count(0)
                                .build()
                );
            }
        }
        return result;
    }
}