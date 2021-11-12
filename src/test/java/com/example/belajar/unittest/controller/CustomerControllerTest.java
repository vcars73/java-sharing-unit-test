package com.example.belajar.unittest.controller;

import com.alibaba.fastjson.JSON;
import com.example.belajar.unittest.model.request.*;
import com.example.belajar.unittest.model.response.CatalogResponse;
import com.example.belajar.unittest.model.response.ListCatalogResponse;
import com.example.belajar.unittest.model.response.SessionResponse;
import com.example.belajar.unittest.model.response.ValidationResponse;
import com.example.belajar.unittest.service.*;
import com.example.belajar.unittest.util.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(MockitoExtension.class)
public class CustomerControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private CustomerController customerController;

    @Mock
    private GetCatalogService getCatalogService;

    @Mock
    private LoginService loginService;

    @Mock
    private LogoutService logoutService;

    @Mock
    private RegisterService registerService;

    @Mock
    private SaveCatalogService saveCatalogService;

    private String accessToken = "accessToken";
    private HttpHeaders headers;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void initMocks(){
        objectMapper = new ObjectMapper();
        headers = new HttpHeaders();
        headers.add("accessToken", accessToken);
        mockMvc = MockMvcBuilders.standaloneSetup(customerController)
                .build();
    }

    @Test
    void shoudlSuccessLogin() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .username("vcars73")
                .password("vcars73")
                .build();

        SessionResponse sessionResponse = SessionResponse.builder()
                .username("vcars73")
                .fullname("Ahmad Zulfikar")
                .accessToken(accessToken)
                .build();
        when(loginService.execute(loginRequest)).thenReturn(sessionResponse);
        MockHttpServletRequestBuilder payload = MockMvcRequestBuilders
                .post("/api/customers/v1/login")
                .content(JSON.toJSONString(loginRequest))
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE);
        MockHttpServletResponse actualResponse = mockMvc.perform(payload)
                .andDo(print())
                .andReturn().getResponse();
        Response expected = new Response(sessionResponse, "Login berhasil", true);
        assertEquals(200, actualResponse.getStatus());
        Assertions.assertEquals(objectMapper.writeValueAsString(expected),actualResponse.getContentAsString());
        verify(loginService).execute(loginRequest);
    }

    @Test
    void shoudlSuccessRegister() throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email("vcars73@gmail.com")
                .fullname("Ahmad Zulfikar")
                .username("vcars73")
                .password("vcars73")
                .build();

        ValidationResponse validationResponse = ValidationResponse.builder().result(true).build();
        when(registerService.execute(registerRequest)).thenReturn(validationResponse);
        MockHttpServletRequestBuilder payload = MockMvcRequestBuilders
                .post("/api/customers/v1/register")
                .content(JSON.toJSONString(registerRequest))
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE);
        MockHttpServletResponse actualResponse = mockMvc.perform(payload)
                .andDo(print())
                .andReturn().getResponse();
        Response expected = new Response(validationResponse.getResult(), "Pendaftaran berhasil", true);
        assertEquals(200, actualResponse.getStatus());
        Assertions.assertEquals(objectMapper.writeValueAsString(expected),actualResponse.getContentAsString());
        verify(registerService).execute(registerRequest);
    }

    @Test
    void shoudlSuccessLogout() throws Exception {
        AccessTokenRequest accessTokenRequest = AccessTokenRequest.builder().accessToken(accessToken).build();

        ValidationResponse validationResponse = ValidationResponse.builder().result(true).build();
        when(logoutService.execute(accessTokenRequest)).thenReturn(validationResponse);
        MockHttpServletRequestBuilder payload = MockMvcRequestBuilders
                .post("/api/customers/v1/logout")
                .headers(headers)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE);
        MockHttpServletResponse actualResponse = mockMvc.perform(payload)
                .andDo(print())
                .andReturn().getResponse();
        Response expected = new Response(validationResponse.getResult(), "Logout berhasil", true);
        assertEquals(200, actualResponse.getStatus());
        Assertions.assertEquals(objectMapper.writeValueAsString(expected),actualResponse.getContentAsString());
        verify(logoutService).execute(accessTokenRequest);
    }


    @Test
    void shoudlSuccessGetCatalog() throws Exception {
        AccessTokenRequest accessTokenRequest = AccessTokenRequest.builder().accessToken(accessToken).build();
        List<CatalogResponse> catalogResponseList = new ArrayList<>();
        catalogResponseList.add(CatalogResponse.builder()
                .catalogName("Category Name 1")
                .price(1000.00)
                .stock(200)
                .build());
        when(getCatalogService.execute(accessTokenRequest)).thenReturn(ListCatalogResponse.builder().catalogResponseList(catalogResponseList).build());
        MockHttpServletRequestBuilder payload = MockMvcRequestBuilders
                .get("/api/customers/v1/catalog")
                .headers(headers)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE);
        MockHttpServletResponse actualResponse = mockMvc.perform(payload)
                .andDo(print())
                .andReturn().getResponse();
        Response expected = new Response(ListCatalogResponse.builder().catalogResponseList(catalogResponseList).build(), "Ini data anda", true);
        assertEquals(200, actualResponse.getStatus());
        assertEquals(JSON.toJSONString(expected), actualResponse.getContentAsString());
        verify(getCatalogService).execute(AccessTokenRequest.builder().accessToken(accessToken).build());
    }

    @Test
    void shoudlFailedGetCatalog() throws Exception {
        AccessTokenRequest accessTokenRequest = AccessTokenRequest.builder().accessToken(accessToken).build();
        when(getCatalogService.execute(accessTokenRequest)).thenReturn(ListCatalogResponse.builder().catalogResponseList(new ArrayList<>()).build());
        MockHttpServletRequestBuilder payload = MockMvcRequestBuilders
                .get("/api/customers/v1/catalog")
                .headers(headers)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE);
        MockHttpServletResponse actualResponse = mockMvc.perform(payload)
                .andDo(print())
                .andReturn().getResponse();
        assertEquals(404, actualResponse.getStatus());
        assertEquals("Data tidak ditemukan", actualResponse.getErrorMessage());
        verify(getCatalogService).execute(AccessTokenRequest.builder().accessToken(accessToken).build());
    }

    @Test
    void shoudlSuccessSaveCatalog() throws Exception {
        AccessTokenRequest accessTokenRequest = AccessTokenRequest.builder().accessToken(accessToken).build();
        CatalogRequest catalogRequest = CatalogRequest.builder()
                .catalogName("catalog name 1")
                .price(1000000.00)
                .stock(1000)
                .build();

        SaveCatalogRequest saveCatalogRequest = SaveCatalogRequest.builder()
                .catalogRequest(catalogRequest)
                .accessTokenRequest(accessTokenRequest)
                .build();

        ValidationResponse validationResponse = ValidationResponse.builder().result(true).build();
        when(saveCatalogService.execute(saveCatalogRequest)).thenReturn(validationResponse);
        MockHttpServletRequestBuilder payload = MockMvcRequestBuilders
                .post("/api/customers/v1/catalog")
                .content(JSON.toJSONString(catalogRequest))
                .headers(headers)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE);
        MockHttpServletResponse actualResponse = mockMvc.perform(payload)
                .andDo(print())
                .andReturn().getResponse();
        Response expected = new Response(null, "Data berhasil dimasukkan", true);
        assertEquals(200, actualResponse.getStatus());
        Assertions.assertEquals(objectMapper.writeValueAsString(expected),actualResponse.getContentAsString());
        verify(saveCatalogService).execute(saveCatalogRequest);
    }

    @Test
    void shoudlFailedSaveCatalog() throws Exception {
        AccessTokenRequest accessTokenRequest = AccessTokenRequest.builder().accessToken(accessToken).build();
        CatalogRequest catalogRequest = CatalogRequest.builder()
                .catalogName("catalog name 1")
                .price(1000000.00)
                .stock(1000)
                .build();

        SaveCatalogRequest saveCatalogRequest = SaveCatalogRequest.builder()
                .catalogRequest(catalogRequest)
                .accessTokenRequest(accessTokenRequest)
                .build();

        ValidationResponse validationResponse = ValidationResponse.builder().result(false).build();
        when(saveCatalogService.execute(saveCatalogRequest)).thenReturn(validationResponse);
        MockHttpServletRequestBuilder payload = MockMvcRequestBuilders
                .post("/api/customers/v1/catalog")
                .content(JSON.toJSONString(catalogRequest))
                .headers(headers)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE);
        MockHttpServletResponse actualResponse = mockMvc.perform(payload)
                .andDo(print())
                .andReturn().getResponse();
        Response expected = new Response(null, "Data berhasil dimasukkan", true);
        assertEquals(500, actualResponse.getStatus());
        assertEquals("Data tidak berhasil dimasukkan", actualResponse.getErrorMessage());
        verify(saveCatalogService).execute(saveCatalogRequest);
    }

}
