package com.market.MSA.requests.filters;

import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InventoryProductFilterRequest {
    Long inventoryId;
    Long productId;
    Double minStock;
    Double maxStock;
    Double minPrice;
    Double maxPrice;
    
    // Sorting
    @Builder.Default
    String sortBy = "stockNumber";
    
    @Builder.Default
    String sortDirection = "DESC";
    
    // Pagination
    @Min(value = 1, message = "Page number must be greater than 0")
    @Builder.Default
    int page = 1;

    @Min(value = 1, message = "Page size must be greater than 0")
    @Builder.Default
    int pageSize = 10;
}
