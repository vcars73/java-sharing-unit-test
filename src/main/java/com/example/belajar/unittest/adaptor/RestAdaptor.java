package com.example.belajar.unittest.adaptor;

import com.alibaba.fastjson.JSON;
import com.example.belajar.unittest.model.request.EsbRequest;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public abstract class RestAdaptor<T,V> {
    private static final Logger log = LogManager.getLogger(RestAdaptor.class);
    protected String url;
    protected HttpMethod httpMethod;
    protected Class<V> response;
    protected RestTemplate restTemplate;
    protected Boolean enableDebug = false;

    public RestAdaptor() {
    }

    protected ResponseEntity getResponse(EsbRequest request) {
        String requestUrl;
        if (ObjectUtils.isNotEmpty(request.getParams()) && !request.getParams().isEmpty()) {
            LinkedMultiValueMap<String, String> params = request.getParams();
            log.info("params = {}", params);
            UriComponents builder = UriComponentsBuilder.fromUriString(this.url).queryParams(params).build();
            requestUrl = builder.toUriString();
        } else {
            requestUrl = this.url;
        }

        if (this.getEnableDebug()) {
            log.info("requestUrl = {}", requestUrl);
            log.info("requestPayload = {}", JSON.toJSONString(request.getPayload()));
        }

        ResponseEntity responseEntity = request.getIsPlain() ? this.restTemplate.exchange(requestUrl, this.httpMethod, request.getPayload(), String.class, new Object[0]) : this.restTemplate.exchange(requestUrl, this.httpMethod, request.getPayload(), this.response, new Object[0]);
        if (this.getEnableDebug()) {
            log.info("response for {} = {}", this.url, responseEntity);
        }

        return responseEntity;
    }

    public abstract V execute(T request);

    protected abstract EsbRequest generatePayload(T request);

    public void setUrl(final String url) {
        this.url = url;
    }

    public void setHttpMethod(final HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public void setResponse(final Class<V> response) {
        this.response = response;
    }

    public void setRestTemplate(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void setEnableDebug(final Boolean enableDebug) {
        this.enableDebug = enableDebug;
    }

    public String getUrl() {
        return this.url;
    }

    public HttpMethod getHttpMethod() {
        return this.httpMethod;
    }

    public Class<V> getResponse() {
        return this.response;
    }

    public RestTemplate getRestTemplate() {
        return this.restTemplate;
    }

    public Boolean getEnableDebug() {
        return this.enableDebug;
    }
}