package com.example.belajar.unittest.service;

import com.example.belajar.unittest.model.entity.Customers;
import com.example.belajar.unittest.model.request.RegisterRequest;
import com.example.belajar.unittest.model.response.ValidationResponse;
import com.example.belajar.unittest.repository.CustomersRepository;
import org.hibernate.JDBCException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RegisterServiceTest {

    @InjectMocks
    private RegisterService registerService;

    @Mock
    private CustomersRepository customersRepository;

    private Customers customers;
    private RegisterRequest registerRequest;

    @BeforeEach
    public void initMocks(){
        customers = new Customers();
        customers.setEmail("vcars73@gmail.com");
        customers.setUsername("vcars73");
        customers.setFullname("vcars73");
        customers.setPassword("vcars73");
        customers.setIsDeleted(false);
        customers.setCreatedBy("SYSTEM");

        registerRequest = RegisterRequest.builder()
                .username("vcars73")
                .password("vcars73")
                .fullname("vcars73")
                .email("vcars73@gmail.com")
                .build();
    }

    @Test
    public void shouldSuccessRegisterService(){
        when(customersRepository.getUserByUsername(registerRequest.getUsername())).thenReturn(Optional.empty());
        when(customersRepository.save(customers)).thenReturn(customers);

        ValidationResponse validationResponse = registerService.execute(registerRequest);
        Assertions.assertTrue(validationResponse.getResult());
    }

    @Test
    public void shouldSuccessFailedSaveCustomerData(){
        when(customersRepository.getUserByUsername(registerRequest.getUsername())).thenReturn(Optional.empty());
        when(customersRepository.save(customers)).thenThrow(new JDBCException("Error",new SQLException()));
        try {
            registerService.execute(registerRequest);
        }
        catch (ResponseStatusException re){
            Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,re.getStatus());
        }
    }

    @Test
    public void shouldFailedUsernameAlreadyExist(){
        when(customersRepository.getUserByUsername(registerRequest.getUsername())).thenReturn(Optional.ofNullable(customers));

        try {
            registerService.execute(registerRequest);
        }
        catch (ResponseStatusException re){
            Assertions.assertEquals(HttpStatus.BAD_REQUEST,re.getStatus());
        };
    }

    @Test
    public void shouldFailedUsernameIsRequired(){
        try {
            registerRequest.setUsername(null);
            registerService.execute(registerRequest);
        }
        catch (ResponseStatusException re){
            Assertions.assertEquals(HttpStatus.BAD_REQUEST,re.getStatus());
        };
    }

    @Test
    public void shouldFailedPasswordIsRequired(){
        try {
            registerRequest.setPassword(null);
            registerService.execute(registerRequest);
        }
        catch (ResponseStatusException re){
            Assertions.assertEquals(HttpStatus.BAD_REQUEST,re.getStatus());
        };
    }

    @Test
    public void shouldFailedEmailIsRequired(){
        try {
            registerRequest.setEmail(null);
            registerService.execute(registerRequest);
        }
        catch (ResponseStatusException re){
            Assertions.assertEquals(HttpStatus.BAD_REQUEST,re.getStatus());
        };
    }

    @Test
    public void shouldFailedFullnameIsRequired(){
        try {
            registerRequest.setFullname(null);
            registerService.execute(registerRequest);
        }
        catch (ResponseStatusException re){
            Assertions.assertEquals(HttpStatus.BAD_REQUEST,re.getStatus());
        };
    }
}
