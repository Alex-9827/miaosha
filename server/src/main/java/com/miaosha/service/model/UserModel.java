package com.miaosha.service.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserModel implements Serializable {
    private Integer id;
    private String name;
    private Byte gender;
    private Integer age;
    private String telephone;
    private String registerMode;
    private String role;
    private String encrptPassword;
}
