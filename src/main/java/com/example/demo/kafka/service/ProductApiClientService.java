package com.example.demo.kafka.service;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.common.LocalizedString;
import com.commercetools.api.models.product.ProductChangeNameActionBuilder;
import com.commercetools.api.models.product.ProductDraft;
import com.commercetools.api.models.product.ProductDraftBuilder;
import com.commercetools.api.models.product.ProductUpdate;
import com.commercetools.api.models.product_type.ProductTypeResourceIdentifierBuilder;
import com.example.demo.dto.PimProduct;
import com.example.demo.dto.ProductRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductApiClientService {

    private final ProjectApiRoot apiClient;

    private static final String DEFAULT_PRODUCT_TYPE = "furniture-and-decor";

    public void importProduct(PimProduct pimProduct) {
        try {
            ProductDraft productDraft = mapToProductDraft(pimProduct);
            if (productDraft == null) {
                log.error("Failed to map product: {}", pimProduct);
                return;
            }

            productExists(productDraft.getKey())
                    .thenCompose(exists -> {
                        if (exists) {
                            log.warn("Product with key {} already exists, updating instead.", productDraft.getKey());
                            return updateExistingProduct(productDraft);
                        } else {
                            return createNewProduct(productDraft);
                        }
                    })
                    .exceptionally(throwable -> {
                        log.error("Failed to import product: {}", throwable.getMessage(), throwable);
                        return null;
                    });

        } catch (Exception e) {
            log.error("Error mapping product: {}", e.getMessage(), e);
        }
    }

    private CompletableFuture<Boolean> productExists(String key) {
        return apiClient
                .products()
                .withKey(key)
                .get()
                .execute()
                .thenApply(response -> response.getStatusCode() == 200)
                .exceptionally(ex -> false);
    }

    private CompletableFuture<Void> createNewProduct(ProductDraft productDraft) {
        return apiClient.products()
                        .post(productDraft)
                        .execute()
                        .thenAccept(response -> log.info("✅ Created product {}", response.getBody()
                                                                                         .getKey()));
    }

    private CompletableFuture<Void> updateExistingProduct(ProductDraft productDraft) {
        return apiClient.products()
                        .withKey(productDraft.getKey())
                        .get()
                        .execute()
                        .thenCompose(response -> {
                            var existingProduct = response.getBody();
                            var update = ProductUpdate.builder()
                                                      .version(existingProduct.getVersion())
                                                      .actions(ProductChangeNameActionBuilder.of()
                                                                                             .name(productDraft.getName())
                                                                                             .build())
                                                      .build();
                            return apiClient.products()
                                            .withId(existingProduct.getId())
                                            .post(update)
                                            .execute()
                                            .thenAccept(updated -> log.info("♻️ Updated product {}", updated.getBody()
                                                                                                            .getKey()));
                        });
    }

    private ProductDraft mapToProductDraft(PimProduct pimProduct) {
        if (pimProduct == null || pimProduct.getPayload() == null) {
            return null;
        }

        final ProductRequest payload = pimProduct.getPayload();

        if (!StringUtils.hasText(payload.getName()) || !StringUtils.hasText(payload.getSku())) {
            return null;
        }

        try {
            return ProductDraftBuilder.of()
                                      .productType(ProductTypeResourceIdentifierBuilder.of()
                                                                                       .key(DEFAULT_PRODUCT_TYPE)
                                                                                       .build())
                                      .name(LocalizedString.ofEnglish(payload.getName()))
                                      .description(payload.getDescription() != null ?
                                              LocalizedString.ofEnglish(payload.getDescription()) :
                                              LocalizedString.ofEnglish(""))
                                      .slug(LocalizedString.ofEnglish(payload.getSku()
                                                                             .toLowerCase()))
                                      .key(payload.getSku())
                                      .masterVariant(variant -> variant.sku(payload.getSku()))
                                      .build();
        } catch (Exception e) {
            log.error("Error building ProductDraft: {}", e.getMessage());
            return null;
        }
    }
}
