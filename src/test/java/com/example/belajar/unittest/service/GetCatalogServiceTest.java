package com.example.belajar.unittest.service;

import com.example.belajar.unittest.adaptor.esb.GetCatalogAdaptor;
import com.example.belajar.unittest.model.request.AccessTokenRequest;
import com.example.belajar.unittest.model.request.EmptyRequest;
import com.example.belajar.unittest.model.response.CatalogResponse;
import com.example.belajar.unittest.model.response.ListCatalogResponse;
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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetCatalogServiceTest {

    @InjectMocks
    private GetCatalogService getCatalogService;

    @Mock
    private GetCatalogAdaptor getCatalogAdaptor;

    @Mock
    private CacheUtility cacheUtility;

    private String accessToken = "accessToken";
    private AccessTokenRequest accessTokenRequest;
    private ListCatalogResponse expected;

    @BeforeEach
    public void initMocks(){
        accessTokenRequest = AccessTokenRequest.builder().accessToken(accessToken).build();
        List<CatalogResponse> catalogResponseList = new ArrayList<>();
        catalogResponseList.add(CatalogResponse.builder()
                .catalogName("catalog name 1")
                .price(100000.00)
                .stock(100)
                .build());
        expected = ListCatalogResponse.builder().catalogResponseList(catalogResponseList).build();
    }

    @Test
    public void shouldSuccessGetCatalogService(){
        when(cacheUtility.get(Constants.RDS_CUSTOMER_SESSION,accessToken)).thenReturn("vcars73");
        when(getCatalogAdaptor.execute(new EmptyRequest())).thenReturn(expected);

        ListCatalogResponse actual = getCatalogService.execute(accessTokenRequest);
        Assertions.assertEquals(expected,actual);
    }

    @Test
    public void shouldFailedUnauthorized(){
        when(cacheUtility.get(Constants.RDS_CUSTOMER_SESSION,accessToken)).thenReturn(null);

        try {
            getCatalogService.execute(accessTokenRequest);
        }
        catch (ResponseStatusException re){
            Assertions.assertEquals(HttpStatus.FORBIDDEN,re.getStatus());
        }
    }
    @Test
    public void shouldFailedAccessTokenIsRequired(){
        accessTokenRequest.setAccessToken(null);
        try {
            getCatalogService.execute(accessTokenRequest);
        }
        catch (ResponseStatusException re){
            Assertions.assertEquals(HttpStatus.BAD_REQUEST,re.getStatus());
        }
    }


}
