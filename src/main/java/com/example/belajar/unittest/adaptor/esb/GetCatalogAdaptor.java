package com.example.belajar.unittest.adaptor.esb;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.example.belajar.unittest.adaptor.RestAdaptor;
import com.example.belajar.unittest.model.request.EmptyRequest;
import com.example.belajar.unittest.model.request.EsbRequest;
import com.example.belajar.unittest.model.response.CatalogResponse;
import com.example.belajar.unittest.model.response.ListCatalogResponse;
import com.example.belajar.unittest.util.CacheUtility;
import com.example.belajar.unittest.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;


@Component
@Slf4j
public class GetCatalogAdaptor extends RestAdaptor<EmptyRequest, ListCatalogResponse> {

    @Value("${unittest.esb.masterData.catalog.url}")
    private String catalogUrl;

    @Value("${unittest.session.expired.catalog}")
    private Integer catalogExpired;

    private RestTemplate restTemplate;

    private CacheUtility cacheUtility;

    public GetCatalogAdaptor(RestTemplate restTemplate,
                             CacheUtility cacheUtility){
        this.restTemplate = restTemplate;
        this.cacheUtility = cacheUtility;
    }

    @Override
    public ListCatalogResponse execute(EmptyRequest request) {
        String stringCatalog = cacheUtility.get(Constants.RDS_CATALOG,"");
        if (StringUtils.isNotEmpty(stringCatalog)){
            JSONArray cache = JSONArray.parseArray(stringCatalog);
            return ListCatalogResponse.builder().catalogResponseList(cache.toJavaList(CatalogResponse.class)).build();
        }
        this.setRestTemplate(restTemplate);
        this.setUrl(this.catalogUrl);
        this.setHttpMethod(HttpMethod.GET);
        try {
            ResponseEntity<String> responseEntity = super.getResponse(this.generatePayload(request));
            JSONArray arrayCatalog = JSON.parseArray(responseEntity.getBody());
            log.info("RESPONSE BODY = {}",arrayCatalog);
            ListCatalogResponse catalogResponseList = ListCatalogResponse.builder().catalogResponseList(arrayCatalog.toJavaList(CatalogResponse.class)).build();
            this.cacheUtility.set(Constants.RDS_CATALOG,"",JSON.toJSONString(catalogResponseList.getCatalogResponseList()),catalogExpired);
            return ListCatalogResponse.builder().catalogResponseList(arrayCatalog.toJavaList(CatalogResponse.class)).build();
        }
        catch (HttpClientErrorException | HttpServerErrorException hcee){
            log.error("getAllCatalog.error = {} - {}", hcee.getRawStatusCode(), hcee.getResponseBodyAsString());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, Constants.ERR_MESSAGE_SYSTEM);
        }catch (ResourceAccessException re){
            log.error("Resource Access Exception = {}", re.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, Constants.ERR_MESSAGE_SYSTEM);
        }
    }

    @Override
    protected EsbRequest generatePayload(EmptyRequest request) {
        return EsbRequest.builder()
                .isPlain(true)
                .build();
    }
}
