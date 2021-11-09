package com.example.belajar.unittest.service;

import com.example.belajar.unittest.model.request.AccessTokenRequest;
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

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LogoutServiceTest {

    @InjectMocks
    private LogoutService logoutService;

    @Mock
    private CacheUtility cacheUtility;


    private AccessTokenRequest accessTokenRequest;
    private String accessToken = "accessToken";

    @BeforeEach
    public void initMocks(){
        accessTokenRequest = AccessTokenRequest.builder().accessToken(accessToken).build();
    }

    @Test
    public void shouldSuccessLoginService(){
        when(cacheUtility.get(Constants.RDS_CUSTOMER_SESSION,accessToken)).thenReturn("vcars73");
        doNothing().when(cacheUtility).delete(Constants.RDS_CUSTOMER_SESSION,accessToken);

        ValidationResponse validationResponse = logoutService.execute(accessTokenRequest);
        Assertions.assertTrue(validationResponse.getResult());
    }

    @Test
    public void shouldFailedSessionNotFound(){
        when(cacheUtility.get(Constants.RDS_CUSTOMER_SESSION,accessToken)).thenReturn(null);

        try {
            logoutService.execute(accessTokenRequest);
        }
        catch (ResponseStatusException re){
            Assertions.assertEquals(HttpStatus.FORBIDDEN,re.getStatus());
        }

    }

    @Test
    public void shouldFailedAccessTokenIsRequired(){
        try {
            accessTokenRequest.setAccessToken(null);
            logoutService.execute(accessTokenRequest);
        }
        catch (ResponseStatusException re){
            Assertions.assertEquals(HttpStatus.BAD_REQUEST,re.getStatus());
        }

    }

}
