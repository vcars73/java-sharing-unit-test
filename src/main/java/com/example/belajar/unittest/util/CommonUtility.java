package com.example.belajar.unittest.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Component
public class CommonUtility {

    private CacheUtility cacheUtility;

    public CommonUtility(CacheUtility cacheUtility){
        this.cacheUtility = cacheUtility;
    }
    public String getUuid(){
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public void checkBlockedUser(String username){
        if (StringUtils.isNotEmpty(this.cacheUtility.get(Constants.RDS_CUSTOMER_BLOCKED,username))){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,username);
        }
    }
}
