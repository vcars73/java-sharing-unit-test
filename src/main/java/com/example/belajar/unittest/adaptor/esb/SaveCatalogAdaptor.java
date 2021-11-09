package com.example.belajar.unittest.adaptor.esb;

import com.example.belajar.unittest.adaptor.RestAdaptor;
import com.example.belajar.unittest.model.request.CatalogRequest;
import com.example.belajar.unittest.model.request.EsbRequest;
import com.example.belajar.unittest.model.response.ValidationResponse;
import com.example.belajar.unittest.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
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
public class SaveCatalogAdaptor extends RestAdaptor<CatalogRequest, ValidationResponse> {

    @Value("${unittest.esb.masterData.catalog.url}")
    private String catalogUrl;

    private RestTemplate restTemplate;

    public SaveCatalogAdaptor(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    @Override
    public ValidationResponse execute(CatalogRequest request) {
        this.setRestTemplate(restTemplate);
        this.setUrl(this.catalogUrl);
        this.setHttpMethod(HttpMethod.POST);
        try {
            ResponseEntity<String> responseEntity = super.getResponse(this.generatePayload(request));
            if (!responseEntity.getStatusCode().equals(HttpStatus.OK)){
                return ValidationResponse.builder().result(false).build();
            }
            return ValidationResponse.builder().result(true).build();
        }
        catch (HttpClientErrorException | HttpServerErrorException hcee){
            log.error("getAllProvince.error = {} - {}", hcee.getRawStatusCode(), hcee.getResponseBodyAsString());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, Constants.ERR_MESSAGE_SYSTEM);
        }catch (ResourceAccessException re){
            log.error("Resource Access Exception = {}", re.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, Constants.ERR_MESSAGE_SYSTEM);
        }
    }

    @Override
    protected EsbRequest generatePayload(CatalogRequest request) {
        return EsbRequest.builder()
                .isPlain(true)
                .payload(new HttpEntity<>(request, null))
                .build();
    }

}
