package com.haiemdavang.AnrealShop.service.order;

import com.haiemdavang.AnrealShop.exception.BadRequestException;
import com.haiemdavang.AnrealShop.repository.product.ProductRepository;
import com.haiemdavang.AnrealShop.repository.product.ProductSkuRepository;
import com.haiemdavang.AnrealShop.tech.elasticsearch.service.ProductIndexerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final ProductRepository productRepository;
    private final ProductSkuRepository productSkuRepository;
    private final ProductIndexerService productIndexerService;

    @Transactional
    public boolean deductInventory(String productSkuId, int quantity) {
        try {
            int updatedSkuRows = productSkuRepository.deductStock(productSkuId, quantity);
            if (updatedSkuRows == 0) {
                return false;
            }

            String productId = productSkuRepository.findProductIdBySkuId(productSkuId);

            int updatedProductRows = productRepository.deductStock(productId, quantity);
            if (updatedProductRows == 0) {
                throw new BadRequestException("INCONSISTENT_STOCK_DATA");
            }

            productIndexerService.updateQuantities(productId, quantity);
            return true;

        } catch (Exception e) {
            throw new RuntimeException("DEDUCT_INVENTORY_FAILED");
        }
    }
}