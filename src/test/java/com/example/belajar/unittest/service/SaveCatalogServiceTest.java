package com.example.belajar.unittest.service;


import com.example.belajar.unittest.adaptor.esb.SaveCatalogAdaptor;
import com.example.belajar.unittest.model.request.AccessTokenRequest;
import com.example.belajar.unittest.model.request.CatalogRequest;
import com.example.belajar.unittest.model.request.SaveCatalogRequest;
import com.example.belajar.unittest.model.response.ValidationResponse;
import com.example.belajar.unittest.util.CacheUtility;
import com.example.belajar.unittest.util.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SaveCatalogServiceTest {

    @InjectMocks
    private SaveCatalogService saveCatalogService;

    @Mock
    private SaveCatalogAdaptor saveCatalogAdaptor;

    @Mock
    private CacheUtility cacheUtility;

    private String accessToken;
    private CatalogRequest catalogRequest;
    private SaveCatalogRequest saveCatalogRequest;

    @BeforeEach
    public void initMocks(){
        accessToken = "accessToken";
        catalogRequest = CatalogRequest.builder()
                .catalogName("Catalog name 1")
                .stock(100)
                .price(100000.00)
                .build();
        saveCatalogRequest = SaveCatalogRequest.builder()
                .accessTokenRequest(AccessTokenRequest.builder().accessToken(accessToken).build())
                .catalogRequest(catalogRequest)
                .build();
    }

    @Test
    public void shouldSuccessSaveCatalogService(){
        when(cacheUtility.get(Constants.RDS_CUSTOMER_SESSION,accessToken)).thenReturn("vcars73");
        when(saveCatalogAdaptor.execute(catalogRequest)).thenReturn(ValidationResponse.builder().result(true).build());

        ValidationResponse validationResponse = saveCatalogService.execute(saveCatalogRequest);
        Assertions.assertTrue(validationResponse.getResult());
    }

    @Test
    public void shouldFailedUnauthorized(){
        when(cacheUtility.get(Constants.RDS_CUSTOMER_SESSION,accessToken)).thenReturn("");

        try {
            saveCatalogService.execute(saveCatalogRequest);
        }
        catch (ResponseStatusException re){
            Assertions.assertEquals(HttpStatus.FORBIDDEN,re.getStatus());
        }
    }

    @Test
    public void shouldFailedAccessTokenIsRequired(){
        try {
            saveCatalogRequest.getAccessTokenRequest().setAccessToken(null);
            saveCatalogService.execute(saveCatalogRequest);
        }
        catch (ResponseStatusException re){
            Assertions.assertEquals(HttpStatus.BAD_REQUEST,re.getStatus());
        }
    }

    @Test
    public void shouldFailedCatalogNameIsRequired(){
        try {
            saveCatalogRequest.getCatalogRequest().setCatalogName(null);
            saveCatalogService.execute(saveCatalogRequest);
        }
        catch (ResponseStatusException re){
            Assertions.assertEquals(HttpStatus.BAD_REQUEST,re.getStatus());
        }
    }

    @Test
    public void shouldFailedStockIsRequired(){
        try {
            saveCatalogRequest.getCatalogRequest().setStock(null);
            saveCatalogService.execute(saveCatalogRequest);
        }
        catch (ResponseStatusException re){
            Assertions.assertEquals(HttpStatus.BAD_REQUEST,re.getStatus());
        }
    }

    @Test
    public void shouldFailedPriceIsRequired(){
        try {
            saveCatalogRequest.getCatalogRequest().setPrice(null);
            saveCatalogService.execute(saveCatalogRequest);
        }
        catch (ResponseStatusException re){
            Assertions.assertEquals(HttpStatus.BAD_REQUEST,re.getStatus());
        }
    }
}
