package com.example.belajar.unittest.service;

import com.example.belajar.unittest.model.request.UpdateLastLoginRequest;
import com.example.belajar.unittest.repository.CustomersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UpdateLastLoginService {

    @Autowired
    private CustomersRepository customersRepository;

    public void execute(UpdateLastLoginRequest input){
        customersRepository.findById(input.getCustomerId()).ifPresentOrElse(data->{
            data.setLastLogin(input.getLastLogin());
            customersRepository.save(data);
        },()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Customer tidak di temukan"));
    }

}
