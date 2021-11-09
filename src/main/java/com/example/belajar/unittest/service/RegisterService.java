package com.example.belajar.unittest.service;

import com.example.belajar.unittest.model.entity.Customers;
import com.example.belajar.unittest.model.request.RegisterRequest;
import com.example.belajar.unittest.model.response.ValidationResponse;
import com.example.belajar.unittest.repository.CustomersRepository;
import com.example.belajar.unittest.util.Constants;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.JDBCException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RegisterService {

    private CustomersRepository customersRepository;

    public RegisterService (CustomersRepository customersRepository){
        this.customersRepository = customersRepository;
    }

    public ValidationResponse execute(RegisterRequest request){
        this.doValidate(request);
        if (customersRepository.getUserByUsername(request.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"username telah terpakai");
        }
        Customers customers = new Customers();
        customers.setUsername(request.getUsername());
        customers.setFullname(request.getFullname());
        customers.setPassword(request.getPassword());
        customers.setEmail(request.getEmail());
        customers.setIsDeleted(false);
        customers.setCreatedBy("SYSTEM");
        try {
            customersRepository.save(customers);
        }
        catch (JDBCException je){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, Constants.ERR_MESSAGE_SYSTEM);
        }
        return ValidationResponse.builder().result(true).build();
    }

    private void doValidate(RegisterRequest request){
        if (StringUtils.isEmpty(request.getUsername())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"username tidak boleh kosong");
        }
        if (StringUtils.isEmpty(request.getPassword())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"password tidak boleh kosong");
        }
        if (StringUtils.isEmpty(request.getFullname())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"fullname tidak boleh kosong");
        }
        if (StringUtils.isEmpty(request.getEmail())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"email tidak boleh kosong");
        }
    }
}
