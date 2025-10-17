package com.example.demo.kafka.service;

import com.commercetools.importapi.client.ProjectApiRoot;
import com.commercetools.importapi.models.common.*;
import com.commercetools.importapi.models.importcontainers.ImportContainerDraftBuilder;
import com.commercetools.importapi.models.importrequests.ProductDraftImportRequestBuilder;
import com.commercetools.importapi.models.productdrafts.PriceDraftImportBuilder;
import com.commercetools.importapi.models.productdrafts.ProductDraftImport;
import com.commercetools.importapi.models.productdrafts.ProductDraftImportBuilder;
import com.commercetools.importapi.models.productdrafts.ProductVariantDraftImportBuilder;
import com.example.demo.dto.ProductRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Locale;

@Service
public class ProductImportService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductImportService.class);
    private final ProjectApiRoot importApiClient;

    public ProductImportService(ProjectApiRoot importApiClient) {
        this.importApiClient = importApiClient;
    }

    public void importProduct(ProductRequest productData) {
        LOGGER.info("Starting product import for key: {}", productData.getKey());

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
                       .thenAccept(response -> LOGGER.info("Product import initiated: {}", response.getBody()
                                                                                                   .getOperationStatus()))
                       .exceptionally(throwable -> {
                           LOGGER.error("Failed to import product: {}", throwable.getMessage());
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
                .thenAccept(container -> LOGGER.info("Created import container: {}", container.getBody()
                                                                                              .getKey()))
                .exceptionally(throwable -> {
                    LOGGER.error("Failed to create import container: {}", throwable.getMessage());
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
                                                                                .prices(Collections.singletonList(
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
                                                                                ))
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
