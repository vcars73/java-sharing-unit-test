package com.example.belajar.unittest.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatalogResponse {
    private String catalogName;
    private Double price;
    private Integer stock;
}
