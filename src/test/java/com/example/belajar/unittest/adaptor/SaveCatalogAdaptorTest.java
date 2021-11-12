package com.example.belajar.unittest.adaptor;

import com.example.belajar.unittest.adaptor.esb.SaveCatalogAdaptor;
import com.example.belajar.unittest.model.request.CatalogRequest;
import com.example.belajar.unittest.model.response.ValidationResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SaveCatalogAdaptorTest {

    @InjectMocks
    private SaveCatalogAdaptor saveCatalogAdaptor;

    @Mock
    private RestTemplate restTemplate;

    private String catalogUrl;
    private CatalogRequest catalogRequest;

    @BeforeEach
    public void initMocks(){
        catalogUrl = "catalogUrl";
        catalogRequest = CatalogRequest.builder()
                .catalogName("catalog name 1")
                .price(100000.00)
                .stock(200)
                .build();
    }

    @Test
    public void shouldSuccessSaveCatalogAdaptor(){
        ReflectionTestUtils.setField(saveCatalogAdaptor,"catalogUrl",catalogUrl);
        when(restTemplate.exchange(catalogUrl, HttpMethod.POST,new HttpEntity<>(catalogRequest,null),String.class,new Object[0])).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        ValidationResponse validationResponse = saveCatalogAdaptor.execute(catalogRequest);
        Assertions.assertTrue(validationResponse.getResult());
    }

    @Test
    public void shouldFailedSaveCatalogAdaptorHttpStatusNotOk(){
        ReflectionTestUtils.setField(saveCatalogAdaptor,"catalogUrl",catalogUrl);
        when(restTemplate.exchange(catalogUrl, HttpMethod.POST,new HttpEntity<>(catalogRequest,null),String.class,new Object[0])).thenReturn(new ResponseEntity<>(HttpStatus.CREATED));

        ValidationResponse validationResponse = saveCatalogAdaptor.execute(catalogRequest);
        Assertions.assertFalse(validationResponse.getResult());
    }

    @Test
    public void shouldFailedHttpClientErrorException(){
        ReflectionTestUtils.setField(saveCatalogAdaptor,"catalogUrl",catalogUrl);
        when(restTemplate.exchange(catalogUrl, HttpMethod.POST,new HttpEntity<>(catalogRequest,null),String.class,new Object[0])).thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED));

        try {
            saveCatalogAdaptor.execute(catalogRequest);
        }
        catch (ResponseStatusException re){
            Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,re.getStatus());
        }
    }

    @Test
    public void shouldFailedResourceAccessException(){
        ReflectionTestUtils.setField(saveCatalogAdaptor,"catalogUrl",catalogUrl);
        when(restTemplate.exchange(catalogUrl, HttpMethod.POST,new HttpEntity<>(catalogRequest,null),String.class,new Object[0])).thenThrow(new ResourceAccessException("error"));

        try {
            saveCatalogAdaptor.execute(catalogRequest);
        }
        catch (ResponseStatusException re){
            Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,re.getStatus());
        }
    }

}
