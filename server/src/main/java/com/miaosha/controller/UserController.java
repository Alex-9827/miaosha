package com.miaosha.controller;


import com.miaosha.error.BusinessException;
import com.miaosha.error.EmBusinessError;
import com.miaosha.response.CommonReturnType;
import com.miaosha.service.UserService;
import com.miaosha.service.model.PromoModel;
import com.miaosha.service.model.UserModel;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/user")
@ResponseBody
public class UserController {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    //测试接口
    @RequestMapping(value = "hello", method = {RequestMethod.POST})
    public CommonReturnType hello(@Param(value="username") String username){
        return CommonReturnType.create("Hello " + username);
    }


    @RequestMapping(value = "currentUser", method = {RequestMethod.POST})
    public CommonReturnType currentUser(@Param(value="token") String token) throws BusinessException {
        //校验登录态
        UserModel userModel = userService.validateToken(token);
        if(userModel == null){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN,"用户未登录");
        }
        return CommonReturnType.create(userModel);
    }


    //token作为用户登录的唯一凭证，所有接口都会验证token，查看用户是否登录
    //token为加密字符串，后端解析得到userId，从redis中得到userModel
    //用户退出后，相应的userModel应当销毁
    @RequestMapping(value = "token", method = {RequestMethod.POST})
    public CommonReturnType login(@Param(value = "telephone") String telephone,
                        @Param(value = "password") String password) throws UnsupportedEncodingException, NoSuchAlgorithmException, BusinessException {

        //入参校验
        if(telephone == null || password == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }

        //验证密码，此处应封装之UserService,返回userModel
        UserModel userModel = userService.validatePassword(telephone, this.EncodeByMd5(password));
        if(userModel == null){
            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
        }
        //返回Token
        String token = UUID.randomUUID().toString().replace("-","");
        redisTemplate.opsForValue().set(token, userModel);
        redisTemplate.expire(token, 1, TimeUnit.HOURS);
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        return CommonReturnType.create(map);
    }







    private String EncodeByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //确定计算方法
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64en = new BASE64Encoder();
        //加密字符串
        String newstr = base64en.encode(md5.digest(str.getBytes("utf-8")));
        return newstr;
    }

    @RequestMapping(value = "/selectByName", method = {RequestMethod.POST})
    public CommonReturnType selectUser(@Param(value="name") String name, @Param(value="token") String token) throws BusinessException {
        System.out.println(name + " " + token);
        //校验登录态
        UserModel userModel = userService.validateToken(token);
        if(userModel == null){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN,"用户未登录");
        }
        List<UserModel> list = userService.selectByName(name);
        return CommonReturnType.create(list);
    }


}
