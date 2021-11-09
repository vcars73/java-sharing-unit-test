package com.example.belajar.unittest.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveCatalogRequest {
    private CatalogRequest catalogRequest;
    private AccessTokenRequest accessTokenRequest;
}
