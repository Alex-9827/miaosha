package com.miaosha.service;

import com.miaosha.service.model.UserModel;

import java.util.List;

public interface UserService {
    UserModel getUserById(Integer id);
    UserModel getUserByTelephone(String telephone);
    UserModel validatePassword(String telephone, String encrptPassword);
    UserModel validateToken(String token);
    List<UserModel> selectByName(String name);
}
