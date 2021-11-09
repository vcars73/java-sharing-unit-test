package com.example.belajar.unittest.controller;

import com.example.belajar.unittest.model.request.*;
import com.example.belajar.unittest.model.response.ListCatalogResponse;
import com.example.belajar.unittest.model.response.SessionResponse;
import com.example.belajar.unittest.model.response.ValidationResponse;
import com.example.belajar.unittest.service.*;
import com.example.belajar.unittest.util.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "/api/customers")
public class CustomerController {

    private LoginService loginService;
    private LogoutService logoutService;
    private RegisterService registerService;
    private GetCatalogService getCatalogService;
    private SaveCatalogService saveCatalogService;

    public CustomerController(LoginService loginService,
                              LogoutService logoutService,
                              RegisterService registerService,
                              GetCatalogService getCatalogService,
                              SaveCatalogService saveCatalogService){
        this.loginService = loginService;
        this.logoutService = logoutService;
        this.registerService = registerService;
        this.getCatalogService = getCatalogService;
        this.saveCatalogService = saveCatalogService;
    }
    @PostMapping("/v1/login")
    public ResponseEntity<Response> login(@RequestBody LoginRequest loginRequest){
        SessionResponse sessionResponse = loginService.execute(loginRequest);
        Response response = new Response(sessionResponse, "Login berhasil", true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/v1/register")
    public ResponseEntity<Response> register(@RequestBody RegisterRequest registerRequest){
        ValidationResponse validationResponse = registerService.execute(registerRequest);
        Response response = new Response(validationResponse.getResult(), "Pendaftaran berhasil", true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/v1/logout")
    public ResponseEntity<Response> logout(@RequestHeader String accessToken){
        ValidationResponse validationResponse = logoutService.execute(AccessTokenRequest.builder().accessToken(accessToken).build());
        Response response = new Response(validationResponse.getResult(), "Logout berhasil", true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/v1/catalog")
    public ResponseEntity<Response> getCatalog(@RequestHeader String accessToken){
        ListCatalogResponse listCatalogResponse = getCatalogService.execute(AccessTokenRequest.builder()
                .accessToken(accessToken)
                .build());
        if (listCatalogResponse.getCatalogResponseList().isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Data tidak ditemukan");
        }
        Response response = new Response(listCatalogResponse, "Ini data anda", true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/v1/catalog")
    public ResponseEntity<Response> saveCatalog(@RequestHeader String accessToken,
                                                @RequestBody CatalogRequest request){
        ValidationResponse validationResponse = saveCatalogService.execute(SaveCatalogRequest.builder()
                .accessTokenRequest(AccessTokenRequest.builder().accessToken(accessToken).build())
                .catalogRequest(request)
                .build());
        if (!validationResponse.getResult()){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Data tidak berhasil dimasukkan");
        }
        Response response = new Response(null, "Data berhasil dimasukkan", true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
