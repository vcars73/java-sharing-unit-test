package com.example.belajar.unittest.service;

import com.example.belajar.unittest.model.entity.Customers;
import com.example.belajar.unittest.model.request.UpdateLastLoginRequest;
import com.example.belajar.unittest.repository.CustomersRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateLastLoginServiceTest {

    @InjectMocks
    private UpdateLastLoginService updateLastLoginService;

    @Mock
    private CustomersRepository customersRepository;

    private UpdateLastLoginRequest updateLastLoginRequest;
    private Customers customers;

    @BeforeEach
    public void initMocks(){
        updateLastLoginRequest = UpdateLastLoginRequest.builder().customerId(73l).lastLogin(new Date()).build();
        customers = new Customers();
        customers.setId(73l);
        customers.setFullname("Ahmad Zulfikar");
    }

    @Test
    public void shouldSuccessUpdateLoginService(){
        when(customersRepository.findById(updateLastLoginRequest.getCustomerId())).thenReturn(java.util.Optional.ofNullable(customers));
        customers.setLastLogin(new Date());
        when(customersRepository.save(customers)).thenReturn(customers);
        updateLastLoginService.execute(updateLastLoginRequest);
        verify(customersRepository,times(1)).findById(updateLastLoginRequest.getCustomerId());
        verify(customersRepository,times(1)).save(customers);
    }

    @Test
    public void shouldFailedCustomerNotFound(){
        when(customersRepository.findById(updateLastLoginRequest.getCustomerId())).thenReturn(Optional.empty());

        try {
            updateLastLoginService.execute(updateLastLoginRequest);
        }
        catch (ResponseStatusException re){
            Assertions.assertEquals(HttpStatus.NOT_FOUND,re.getStatus());
        }
    }
}
