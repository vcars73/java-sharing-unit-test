package com.example.belajar.unittest.service;

import com.example.belajar.unittest.model.entity.Customers;
import com.example.belajar.unittest.model.request.LoginRequest;
import com.example.belajar.unittest.model.request.UpdateLastLoginRequest;
import com.example.belajar.unittest.model.response.SessionResponse;
import com.example.belajar.unittest.repository.CustomersRepository;
import com.example.belajar.unittest.util.CacheUtility;
import com.example.belajar.unittest.util.CommonUtility;
import com.example.belajar.unittest.util.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoginServiceTest {

    @InjectMocks
    private LoginService loginService;

    @Mock
    private CustomersRepository customersRepository;

    @Mock
    private CacheUtility cacheUtility;

    @Mock
    private CommonUtility commonUtility;

    @Mock
    private UpdateLastLoginService updateLastLoginService;

    private Customers customers;
    private String accessToken;
    private LoginRequest loginRequest;
    private Integer sessionExpired;
    private Integer cooldown;
    private Integer maxRetryLogin;
    private SessionResponse expected;

    @BeforeEach
    public void initMocks(){
        customers = new Customers();
        customers.setId(73L);
        customers.setUsername("vcars73");
        customers.setFullname("Ahmad Zulfikar");
        customers.setCreatedBy("SYSTEM");
        customers.setEmail("vcars73@gmail.com");
        customers.setRetryLogin(0);
        customers.setPassword("vcars73");
        customers.setIsDeleted(false);

        accessToken = "accessToken";

        loginRequest = LoginRequest.builder()
                .username("vcars73")
                .password("vcars73")
                .build();

        sessionExpired = 600;
        cooldown = 86400;
        maxRetryLogin = 3;
        expected = SessionResponse.builder()
                .fullname(customers.getFullname())
                .username(customers.getUsername())
                .accessToken(accessToken)
                .build();
    }

    @Test
    public void shouldSuccessLoginService(){
        ReflectionTestUtils.setField(loginService, "sessionExpired",sessionExpired);
        when(customersRepository.getUserByUsername(loginRequest.getUsername())).thenReturn(java.util.Optional.of(customers));
        doNothing().when(commonUtility).checkBlockedUser(loginRequest.getUsername());
        lenient().doNothing().when(updateLastLoginService).execute(UpdateLastLoginRequest.builder()
                .customerId(customers.getId())
                .lastLogin(new Date())
                .build());
        when(commonUtility.getUuid()).thenReturn(accessToken);
        doNothing().when(cacheUtility).set(Constants.RDS_CUSTOMER_SESSION,accessToken,customers.getUsername(),sessionExpired);

        SessionResponse sessionResponse = loginService.execute(loginRequest);
        Assertions.assertEquals(expected,sessionResponse);
    }


    @Test
    public void shouldFailedUsernameTidakBolehKosong(){
        loginRequest.setUsername(null);
        try {
            loginService.execute(loginRequest);
        }
        catch (ResponseStatusException re){
            Assertions.assertEquals(HttpStatus.BAD_REQUEST,re.getStatus());
        }
    }

    @Test
    public void shouldFailedPasswordTidakBolehKosong(){
        loginRequest.setPassword(null);
        try {
            loginService.execute(loginRequest);
        }
        catch (ResponseStatusException re){
            Assertions.assertEquals(HttpStatus.BAD_REQUEST,re.getStatus());
        }
    }

    @Test
    public void shouldFailedCustomerNotFound(){
        when(customersRepository.getUserByUsername(loginRequest.getUsername())).thenReturn(Optional.empty());
        try {
            loginService.execute(loginRequest);
        }
        catch (ResponseStatusException re){
            Assertions.assertEquals(HttpStatus.NOT_FOUND,re.getStatus());
        }
    }

    @Test
    public void shouldFailedWrongPassword(){
        ReflectionTestUtils.setField(loginService, "maxLoginRetryLogin",maxRetryLogin);
        customers.setPassword("737373");
        when(customersRepository.getUserByUsername(loginRequest.getUsername())).thenReturn(Optional.ofNullable(customers));
        doNothing().when(commonUtility).checkBlockedUser(loginRequest.getUsername());
        customers.setRetryLogin(customers.getRetryLogin()+1);
        when(customersRepository.save(customers)).thenReturn(customers);
        try {
            loginService.execute(loginRequest);
        }
        catch (ResponseStatusException re){
            Assertions.assertEquals(HttpStatus.FORBIDDEN,re.getStatus());
        }
    }

    @Test
    public void shouldFailedWrongPasswordAndCustomerRetryLoginIsNull(){
        ReflectionTestUtils.setField(loginService, "maxLoginRetryLogin",maxRetryLogin);
        customers.setRetryLogin(null);
        customers.setPassword("737373");
        when(customersRepository.getUserByUsername(loginRequest.getUsername())).thenReturn(Optional.ofNullable(customers));
        doNothing().when(commonUtility).checkBlockedUser(loginRequest.getUsername());
        when(customersRepository.save(customers)).thenReturn(customers);
        try {
            loginService.execute(loginRequest);
        }
        catch (ResponseStatusException re){
            Assertions.assertEquals(HttpStatus.FORBIDDEN,re.getStatus());
        }
    }

    @Test
    public void shouldFailedWrongPasswordLimitReach(){
        ReflectionTestUtils.setField(loginService, "maxLoginRetryLogin",maxRetryLogin);
        customers.setRetryLogin(7);
        customers.setPassword("737373");
        when(customersRepository.getUserByUsername(loginRequest.getUsername())).thenReturn(Optional.ofNullable(customers));
        doNothing().when(commonUtility).checkBlockedUser(loginRequest.getUsername());
        cacheUtility.set(Constants.RDS_CUSTOMER_BLOCKED,customers.getUsername(),"LIMIT RETRY LOGIN",cooldown);
        when(customersRepository.save(customers)).thenReturn(customers);
        try {
            loginService.execute(loginRequest);
        }
        catch (ResponseStatusException re){
            Assertions.assertEquals(HttpStatus.BAD_REQUEST,re.getStatus());
        }
    }

}
