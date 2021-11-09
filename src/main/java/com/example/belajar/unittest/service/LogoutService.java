package com.example.belajar.unittest.service;

import com.example.belajar.unittest.model.request.AccessTokenRequest;
import com.example.belajar.unittest.model.response.ValidationResponse;
import com.example.belajar.unittest.util.CacheUtility;
import com.example.belajar.unittest.util.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LogoutService {

    private CacheUtility cacheUtility;

    public LogoutService(CacheUtility cacheUtility){
        this.cacheUtility = cacheUtility;
    }

    public ValidationResponse execute(AccessTokenRequest request){
        if (StringUtils.isEmpty(request.getAccessToken())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"access token tidak boleh kosong");
        }
        if (StringUtils.isEmpty(cacheUtility.get(Constants.RDS_CUSTOMER_SESSION,request.getAccessToken()))){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"anda tidak berhak akses");
        }
        cacheUtility.delete(Constants.RDS_CUSTOMER_SESSION,request.getAccessToken());
        return ValidationResponse.builder().result(true).build();
    }

}
