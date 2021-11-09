package com.example.belajar.unittest.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "customers")
public class Customers extends BaseEntity{

    @Id
    @SequenceGenerator(name = "user_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name="username", length = 30)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name="fullname")
    private String fullname;

    @Column(name="email")
    private String email;

    @Column(name = "status")
    private Integer status;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "wrong_password")
    private Integer wrongPassword;

    @Column(name = "updated_password_date")
    private Date updatedPasswordDate;

    @Column(name = "last_login")
    private Date lastLogin;

    @Column(name = "retry_login")
    private Integer retryLogin;

}
