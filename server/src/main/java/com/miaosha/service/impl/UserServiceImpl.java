package com.miaosha.service.impl;

import com.miaosha.dao.UserDOMapper;
import com.miaosha.dao.UserPasswordDOMapper;
import com.miaosha.dataobject.UserDO;
import com.miaosha.dataobject.UserPasswordDO;
import com.miaosha.service.UserService;
import com.miaosha.service.model.UserModel;
import org.apache.catalina.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDOMapper userDOMapper;

    @Autowired
    private UserPasswordDOMapper userPasswordDOMapper;

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public UserModel getUserById(Integer id) {
        UserDO userDO = userDOMapper.selectByPrimaryKey(id);
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUid(id);
        UserModel userModel = convertFromDataObject(userDO, userPasswordDO);
        return userModel;
    }

    @Override
    public UserModel getUserByTelephone(String telephone) {
        UserDO userDO = userDOMapper.selectByTelephone(telephone);
        return this.getUserById(userDO.getId());
    }




    @Override
    public UserModel validatePassword(String telephone, String encrptPassword) {
        UserModel userModel = this.getUserByTelephone(telephone);
        if(userModel == null){
            return null;
        }
        if(userModel.getEncrptPassword().equals(encrptPassword) == false){
            return null;
        }
        return userModel;
    }

    @Override
    public UserModel validateToken(String token) {
        //验证token是否合法，若合法，返回用户登录态
        //TODO 复杂化Token
        UserModel userModel = (UserModel) redisTemplate.opsForValue().get(token);
        return userModel;
    }

    @Override
    public List<UserModel> selectByName(String name) {
        List<UserModel> list = new ArrayList<>();
        List<UserDO> l = userDOMapper.selectByName(name);
        for(UserDO userDO : l){
            list.add(convertFromDataObject(userDO, null));
        }
        return list;
    }

    private UserModel convertFromDataObject(UserDO userDO, UserPasswordDO userPasswordDO){
        if(userDO == null){
            return null;
        }
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userDO,userModel);

        if(userPasswordDO != null){
            userModel.setEncrptPassword(userPasswordDO.getEncrptPassword());
        }

        return userModel;
    }


}
