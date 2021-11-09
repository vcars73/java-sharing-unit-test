package com.example.belajar.unittest.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatalogRequest {
    private String catalogName;
    private Double price;
    private Integer stock;
}
