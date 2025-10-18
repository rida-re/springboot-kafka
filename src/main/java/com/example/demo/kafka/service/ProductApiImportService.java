package com.example.demo.kafka.service;

import com.commercetools.importapi.client.ProjectApiRoot;
import com.commercetools.importapi.models.common.*;
import com.commercetools.importapi.models.importcontainers.ImportContainerDraftBuilder;
import com.commercetools.importapi.models.importoperations.ImportOperation;
import com.commercetools.importapi.models.importrequests.ProductDraftImportRequestBuilder;
import com.commercetools.importapi.models.productdrafts.ProductDraftImport;
import com.commercetools.importapi.models.productdrafts.ProductDraftImportBuilder;
import com.commercetools.importapi.models.productdrafts.ProductVariantDraftImportBuilder;
import com.example.demo.dto.ProductRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductApiImportService {

    private static final String CONTAINER_KEY = "products-container";
    private final ProjectApiRoot importApiClient;

    public void importProduct(ProductRequest productData) {

        createImportContainerIfNotExists(CONTAINER_KEY);

        ProductDraftImport productImport = createProductDraftImport(productData);

        importApiClient.productDrafts()
                       .importContainers()
                       .withImportContainerKeyValue(CONTAINER_KEY)
                       .post(ProductDraftImportRequestBuilder.of()
                                                             .resources(Collections.singletonList(productImport))
                                                             .build()
                       )
                       .execute()
                       .thenAccept(resp -> log.info("Import submitted: {}", resp.getBody().getOperationStatus()))
                       .exceptionally(t -> {
                           log.error("Import failed", t);
                           return null;
                       });

        final List<ImportOperation> importOperations = importApiClient
                .importContainers()
                .withImportContainerKeyValue(CONTAINER_KEY)
                .importOperations()
                .get()
                .executeBlocking()
                .getBody()
                .getResults();

        for (ImportOperation io : importOperations) {
            log.info("Resource: " + io.getResourceKey() + " State: " + io.getState());
        }

    }

    private void createImportContainerIfNotExists(String containerKey) {
        importApiClient.importContainers()
                       .withImportContainerKeyValue(containerKey)
                       .get()
                       .execute()
                       .whenComplete((response, throwable) -> {
                           if (throwable != null) {
                               // Si une exception est lancée (404, etc.)
                               if (throwable.getCause() instanceof io.vrap.rmf.base.client.error.NotFoundException) {
                                   log.info("Import container {} not found, creating it...", containerKey);
                                   importApiClient.importContainers()
                                                  .post(ImportContainerDraftBuilder.of()
                                                                                   .key(containerKey)
                                                                                   .resourceType(ImportResourceType.PRODUCT)
                                                                                   .build())
                                                  .execute()
                                                  .thenAccept(created -> log.info("✅ Created import container {}", created.getBody().getKey()))
                                                  .exceptionally(e -> {
                                                      log.error("❌ Failed to create import container {}", containerKey, e);
                                                      return null;
                                                  });
                               } else {
                                   log.error("❌ Failed to check import container {}", containerKey, throwable);
                               }
                           } else {
                               log.info("✅ Import container {} already exists", containerKey);
                           }
                       });
    }

    private ProductDraftImport createProductDraftImport(ProductRequest data) {
        return ProductDraftImportBuilder.of()
                                        .key(data.getKey())
                                        .name(LocalizedStringBuilder.of().addValue(Locale.ENGLISH.toString(), data.getName()).build())
                                        .slug(LocalizedStringBuilder.of().addValue(Locale.ENGLISH.toString(), data.getSlug()).build())
                                        .productType(ProductTypeKeyReferenceBuilder.of().key(data.getProductTypeId()).build())
                                        .description(LocalizedStringBuilder.of().addValue(Locale.ENGLISH.toString(), data.getDescription()).build())
                                        .masterVariant(ProductVariantDraftImportBuilder.of()
                                                                                       .sku(data.getSku())
                                                                                       .key(data.getKey())
                                                                                       .images(Collections.singletonList(
                                                                                               ImageBuilder.of()
                                                                                                           .url(data.getImage().getUrl())
                                                                                                           .dimensions(AssetDimensionsBuilder.of()
                                                                                                                                             .h(data.getImage().getDimensions().getH())
                                                                                                                                             .w(data.getImage().getDimensions().getW())
                                                                                                                                             .build())
                                                                                                           .build()
                                                                                       ))
                                                                                       .build())
                                        .build();
    }
}
