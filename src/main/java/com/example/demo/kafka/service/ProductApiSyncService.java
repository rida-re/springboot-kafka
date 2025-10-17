package com.example.demo.kafka.service;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.category.CategoryResourceIdentifier;
import com.commercetools.api.models.category.CategoryResourceIdentifierBuilder;
import com.commercetools.api.models.common.*;
import com.commercetools.api.models.product.*;
import com.commercetools.api.models.product_type.ProductTypeResourceIdentifierBuilder;
import com.commercetools.sync.products.ProductSync;
import com.commercetools.sync.products.ProductSyncOptions;
import com.commercetools.sync.products.ProductSyncOptionsBuilder;
import com.example.demo.dto.ProductRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductApiSyncService {

    private final ProjectApiRoot client;
    private final ProductSync productSync;

    public ProductApiSyncService(ProjectApiRoot client) {
        this.client = client;
        this.productSync = new ProductSync(createSyncOptions());
    }

    private ProductSyncOptions createSyncOptions() {
        // Note: errorCallback and warningCallback have multiple parameters; ignore the ones we don't use.
        return ProductSyncOptionsBuilder.of(client)
                                        .errorCallback((exception, oldResource, newResource, updateActions) ->
                                                log.error("Error when syncing product.", exception))
                                        .warningCallback((warning, oldResource, newResource) ->
                                                log.warn("Warning when syncing product: {}", warning.getMessage()))
                                        .build();
    }

    public void syncProduct(ProductRequest productData) {
        ProductDraft productDraft = convertToProductDraft(productData);

        productSync.sync(Collections.singletonList(productDraft))
                   .whenComplete((result, throwable) -> {
                       if (throwable != null) {
                           log.error("Product sync failed with exception.", throwable);
                           return;
                       }
                       if (result == null) {
                           log.error("Product sync returned null result.");
                           return;
                       }
                       if (result.getFailed()
                                 .get() > 0) {
                           log.error("Product sync has failures: {}", result.getReportMessage());
                       } else {
                           log.info("Product sync completed successfully: {}", result.getReportMessage());
                       }
                       log.info("Product sync completed successfully: {}", result.getReportMessage());

                   });
    }

    private ProductDraft convertToProductDraft(ProductRequest productData) {
        return ProductDraftBuilder.of()
                                  .key(productData.getKey())
                                  .name(LocalizedString.of(Locale.ENGLISH, productData.getName()))
                                  .productType(ProductTypeResourceIdentifierBuilder.of()
                                                                                   .id(productData.getProductTypeId())
                                                                                   .build())
                                  .slug(LocalizedString.of(Locale.ENGLISH, productData.getSlug() != null ?
                                          productData.getSlug() : productData.getKey()))
                                  .description(LocalizedString.of(Locale.ENGLISH, productData.getDescription()))
                                  .categories(createCategoryReferences(productData.getCategoryIds()))
                                  .masterVariant(createMasterVariant(productData))
                                  .build();

    }

    private List<CategoryResourceIdentifier> createCategoryReferences(List<String> categoryIds) {
        if (categoryIds == null) return Collections.emptyList();
        return categoryIds.stream()
                          .map(id -> CategoryResourceIdentifierBuilder.of()
                                                                      .id(id)
                                                                      .build())
                          .collect(Collectors.toList());
    }

    private ProductVariantDraft createMasterVariant(ProductRequest productData) {
        return ProductVariantDraftBuilder.of()
                                         .key(productData.getKey())
                                         .sku(productData.getSku() != null ? productData.getSku() : productData.getKey())
                                         //.prices(createPrices(productData.getPrice()))
                                         .images(createImages(productData.getImage()))
                                         .attributes(createAttributes(productData.getAttributes()))
                                         .build();
    }

    private List<PriceDraft> createPrices(ProductRequest.PriceInfo priceInfo) {
        if (priceInfo == null) return Collections.emptyList();

        return Collections.singletonList(
                PriceDraftBuilder.of()
                                 .value(MoneyBuilder.of()
                                                    .centAmount(priceInfo.getCentAmount())
                                                    .currencyCode(priceInfo.getCurrencyCode())
                                                    .build())
                                 .build()
        );
    }

    private List<Image> createImages(ProductRequest.ImageInfo imageInfo) {
        if (imageInfo == null) return Collections.emptyList();

        return Collections.singletonList(
                Image.builder()
                     .url(imageInfo.getUrl())
                     .label(imageInfo.getLabel())
                     .dimensions(ImageDimensionsBuilder.of()
                                                       .w(imageInfo.getDimensions()
                                                                   .getW())
                                                       .h(imageInfo.getDimensions()
                                                                   .getH())
                                                       .build())
                     .build()
        );
    }

    private List<Attribute> createAttributes(Map<String, Object> attributes) {
        if (attributes == null) return Collections.emptyList();
        return attributes.entrySet()
                         .stream()
                         .map(entry -> {
                             Object value = entry.getValue();
                             return AttributeBuilder.of()
                                                    .name(entry.getKey())
                                                    .value(value)
                                                    .build();
                         })
                         .collect(Collectors.toList());
    }
}

