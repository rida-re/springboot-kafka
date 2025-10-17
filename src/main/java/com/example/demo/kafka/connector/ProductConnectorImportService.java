package com.example.demo.kafka.connector;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.common.LocalizedString;
import com.commercetools.api.models.product.ProductDraft;
import com.commercetools.api.models.product.ProductDraftBuilder;
import com.commercetools.api.models.product_type.ProductTypeResourceIdentifierBuilder;
import com.example.demo.dto.PimProduct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductConnectorImportService {

    private final ProjectApiRoot commerceToolsClient;
    private static final String DEFAULT_PRODUCT_TYPE = "furniture-and-decor";

    public void importProduct(PimProduct pimProduct) {
        try {
            ProductDraft productDraft = mapToProductDraft(pimProduct);
            if (productDraft == null) {
                log.error("Failed to map product: {}", pimProduct);
                return;
            }

            commerceToolsClient.products()
                .post(productDraft)
                .execute()
                .thenAccept(product -> 
                    log.info("Product successfully imported: {}", product.getBody().getId()))
                .exceptionally(throwable -> {
                    log.error("Failed to import product: {}", throwable.getMessage());
                    return null;
                });
        } catch (Exception e) {
            log.error("Error mapping product: {}", e.getMessage(), e);
        }
    }

    private ProductDraft mapToProductDraft(PimProduct pimProduct) {
        if (pimProduct == null || pimProduct.getPayload() == null) {
            return null;
        }

        PimProduct.ProductPayload payload = pimProduct.getPayload();
        
        if (!StringUtils.hasText(payload.getName()) || !StringUtils.hasText(payload.getSku())) {
            return null;
        }

        try {
            return ProductDraftBuilder.of()
                .productType(ProductTypeResourceIdentifierBuilder.of()
                    .key(DEFAULT_PRODUCT_TYPE)
                    .build())
                .name(LocalizedString.ofEnglish(payload.getName()))
                .description(payload.getDescription() != null 
                    ? LocalizedString.ofEnglish(payload.getDescription())
                    : LocalizedString.ofEnglish(""))
                .slug(LocalizedString.ofEnglish(payload.getSku().toLowerCase()))
                .key(payload.getSku())
                .masterVariant(variant -> variant.sku(payload.getSku()))
                .build();
        } catch (Exception e) {
            log.error("Error building ProductDraft: {}", e.getMessage());
            return null;
        }
    }
}
