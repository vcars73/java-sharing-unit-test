package com.example.belajar.unittest.repository;

import com.example.belajar.unittest.model.entity.Customers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomersRepository extends JpaRepository<Customers,Long> {

    @Query(value="select * from customers u where u.username=:username", nativeQuery = true)
    Optional<Customers> getUserByUsername(@Param("username") String username);

}
