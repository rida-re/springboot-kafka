package com.example.demo.kafka.service;

import com.commercetools.importapi.client.ProjectApiRoot;
import com.commercetools.importapi.models.common.AssetDimensionsBuilder;
import com.commercetools.importapi.models.common.ImageBuilder;
import com.commercetools.importapi.models.common.LocalizedStringBuilder;
import com.commercetools.importapi.models.common.ProductTypeKeyReferenceBuilder;
import com.commercetools.importapi.models.importcontainers.ImportContainerDraftBuilder;
import com.commercetools.importapi.models.importrequests.ProductDraftImportRequestBuilder;
import com.commercetools.importapi.models.productdrafts.ProductDraftImport;
import com.commercetools.importapi.models.productdrafts.ProductDraftImportBuilder;
import com.commercetools.importapi.models.productdrafts.ProductVariantDraftImportBuilder;
import com.example.demo.dto.ProductRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductApiImportService {

    private final ProjectApiRoot importApiClient;

    public void importProduct(ProductRequest productData) {
        log.info("Starting product import for key: {}", productData.getKey());

        // Create import container if not exists
        String containerKey = "products-" + System.currentTimeMillis();
        createImportContainer(containerKey);

        // Create product draft
        ProductDraftImport productImport = createProductDraftImport(productData);

        // Import product
        importApiClient.productDrafts()
                       .importContainers()
                       .withImportContainerKeyValue(containerKey)
                       .post(
                               ProductDraftImportRequestBuilder.of()
                                                               .resources(productImport)
                                                               .build()
                       )
                       .execute()
                       .thenAccept(response -> log.info("Product import initiated: {}", response.getBody()
                                                                                                .getOperationStatus()))
                       .exceptionally(throwable -> {
                           log.error("Failed to import product: {}", throwable.getMessage());
                           return null;
                       });

    }

    private void createImportContainer(String containerKey) {
        importApiClient
                .importContainers()
                .post(
                        ImportContainerDraftBuilder.of()
                                                   .key(containerKey)
                                                   .build()
                )
                .execute()
                .thenAccept(container -> log.info("Created import container: {}", container.getBody()
                                                                                           .getKey()))
                .exceptionally(throwable -> {
                    log.error("Failed to create import container: {}", throwable.getMessage());
                    return null;
                });
    }

    private ProductDraftImport createProductDraftImport(ProductRequest productData) {
        return ProductDraftImportBuilder.of()
                                        .key(productData.getKey())
                                        .name(LocalizedStringBuilder.of()
                                                                    .addValue(Locale.ENGLISH.toString(), productData.getName())
                                                                    .build())
                                        .productType(
                                                ProductTypeKeyReferenceBuilder.of()
                                                                              .key(productData.getProductTypeId())
                                                                              .build()
                                        )
                                        .slug(LocalizedStringBuilder.of()
                                                                    .addValue(Locale.ENGLISH.toString(), productData.getSlug())
                                                                    .build())
                                        .description(LocalizedStringBuilder.of()
                                                                           .addValue(Locale.ENGLISH.toString(), productData.getDescription())
                                                                           .build())
                                        .masterVariant(
                                                ProductVariantDraftImportBuilder.of()
                                                                                .sku(productData.getSku())
                                                                                .key(productData.getKey())
                                                                                /* .prices(Collections.singletonList(
                                                                                         PriceDraftImportBuilder.of()
                                                                                                                .key("price-" + System.currentTimeMillis())
                                                                                                                .value(
                                                                                                                        MoneyBuilder.of()
                                                                                                                                    .centAmount(productData.getPrice()
                                                                                                                                                           .getCentAmount())
                                                                                                                                    .currencyCode(productData.getPrice()
                                                                                                                                                             .getCurrencyCode())
                                                                                                                                    .build()
                                                                                                                )
                                                                                                                .build()
                                                                                 )) */
                                                                                .images(Collections.singletonList(
                                                                                        ImageBuilder.of()
                                                                                                    .url(productData.getImage()
                                                                                                                    .getUrl())
                                                                                                    .dimensions(AssetDimensionsBuilder.of()
                                                                                                                                      .h(productData.getImage()
                                                                                                                                                    .getDimensions()
                                                                                                                                                    .getH())
                                                                                                                                      .w(productData.getImage()
                                                                                                                                                    .getDimensions()
                                                                                                                                                    .getW())
                                                                                                                                      .build())
                                                                                                    .build()
                                                                                ))
                                                                                .build()
                                        )
                                        .build();
    }
}
