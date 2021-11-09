package com.example.belajar.unittest.service;

import com.example.belajar.unittest.adaptor.esb.SaveCatalogAdaptor;
import com.example.belajar.unittest.model.request.SaveCatalogRequest;
import com.example.belajar.unittest.model.response.ValidationResponse;
import com.example.belajar.unittest.util.CacheUtility;
import com.example.belajar.unittest.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
public class SaveCatalogService {

    private SaveCatalogAdaptor saveCatalogAdaptor;
    private CacheUtility cacheUtility;

    public SaveCatalogService (SaveCatalogAdaptor saveCatalogAdaptor,
                               CacheUtility cacheUtility){
        this.saveCatalogAdaptor = saveCatalogAdaptor;
        this.cacheUtility = cacheUtility;
    }

    public ValidationResponse execute(SaveCatalogRequest input){
        doValidateRequest(input);
        String sessionCache = this.cacheUtility.get(Constants.RDS_CUSTOMER_SESSION,input.getAccessTokenRequest().getAccessToken());
        if (StringUtils.isEmpty(sessionCache)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Anda tidak berhak akses");
        }
        return saveCatalogAdaptor.execute(input.getCatalogRequest());
    }

    private void doValidateRequest(SaveCatalogRequest request){
        if (StringUtils.isEmpty(request.getAccessTokenRequest().getAccessToken())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"access token tidak boleh kosong");
        }
        if (StringUtils.isEmpty(request.getCatalogRequest().getCatalogName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"catalog name tidak boleh kosong");
        }
        if (ObjectUtils.isEmpty(request.getCatalogRequest().getStock())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"stok tidak boleh kosong");
        }
        if (ObjectUtils.isEmpty(request.getCatalogRequest().getPrice())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"price tidak boleh kosong");
        }
    }

}
