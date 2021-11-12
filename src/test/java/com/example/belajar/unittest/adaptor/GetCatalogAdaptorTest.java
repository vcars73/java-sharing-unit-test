package com.example.belajar.unittest.adaptor;

import com.alibaba.fastjson.JSON;
import com.example.belajar.unittest.adaptor.esb.GetCatalogAdaptor;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
public class GetCatalogAdaptorTest {

    @InjectMocks
    private GetCatalogAdaptor getCatalogAdaptor;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private CacheUtility cacheUtility;

    private String catalogUrl;
    private Integer catalogExpired;
    private ListCatalogResponse expected;
    private List<CatalogResponse> result;

    @BeforeEach
    public void initMocks(){
        catalogUrl = "catalogUrl";
        catalogExpired = 3600;
        result = new ArrayList<>();
        result.add(CatalogResponse.builder().catalogName("catalog name 1").price(1000.00).stock(10).build());
        expected = ListCatalogResponse.builder().catalogResponseList(result).build();
    }

    @Test
    public void shouldSuccessGetCatalogAdaptor(){
        when(cacheUtility.get(Constants.RDS_CATALOG,"")).thenReturn(null);
        ReflectionTestUtils.setField(getCatalogAdaptor,"catalogUrl",catalogUrl);
        when(restTemplate.exchange(catalogUrl, HttpMethod.GET,null,String.class,new Object[0])).thenReturn(new ResponseEntity<>(JSON.toJSONString(result),null, HttpStatus.OK));
        ReflectionTestUtils.setField(getCatalogAdaptor,"catalogExpired",catalogExpired);
        doNothing().when(cacheUtility).set(Constants.RDS_CATALOG,"",JSON.toJSONString(result),catalogExpired);

        ListCatalogResponse actual = getCatalogAdaptor.execute(new EmptyRequest());
        Assertions.assertEquals(expected,actual);
    }

    @Test
    public void shouldSuccessGetCatalogAdaptorFromCache(){
        when(cacheUtility.get(Constants.RDS_CATALOG,"")).thenReturn(JSON.toJSONString(result));

        ListCatalogResponse actual = getCatalogAdaptor.execute(new EmptyRequest());
        Assertions.assertEquals(expected,actual);
    }

    @Test
    public void shouldFailedHttpClientErrorException(){
        when(cacheUtility.get(Constants.RDS_CATALOG,"")).thenReturn(null);
        ReflectionTestUtils.setField(getCatalogAdaptor,"catalogUrl",catalogUrl);
        when(restTemplate.exchange(catalogUrl, HttpMethod.GET,null,String.class,new Object[0])).thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED));

        try {
            getCatalogAdaptor.execute(new EmptyRequest());
        }
        catch (ResponseStatusException re){
            Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,re.getStatus());
        }
    }

    @Test
    public void shouldFailedResourceAccessException(){
        when(cacheUtility.get(Constants.RDS_CATALOG,"")).thenReturn(null);
        ReflectionTestUtils.setField(getCatalogAdaptor,"catalogUrl",catalogUrl);
        when(restTemplate.exchange(catalogUrl, HttpMethod.GET,null,String.class,new Object[0])).thenThrow(new ResourceAccessException("error"));

        try {
            getCatalogAdaptor.execute(new EmptyRequest());
        }
        catch (ResponseStatusException re){
            Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,re.getStatus());
        }
    }



}
