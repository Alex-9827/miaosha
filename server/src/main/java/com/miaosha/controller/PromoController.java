package com.miaosha.controller;

import com.miaosha.error.BusinessException;
import com.miaosha.error.EmBusinessError;
import com.miaosha.response.CommonReturnType;
import com.miaosha.service.PromoService;
import com.miaosha.service.UserService;
import com.miaosha.service.model.ItemModel;
import com.miaosha.service.model.PromoModel;
import com.miaosha.service.model.UserModel;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@ResponseBody
@RequestMapping("/promo")
public class PromoController {
    @Autowired
    private PromoService promoService;

    @Autowired
    private UserService userService;


    @RequestMapping(value = "/publish", method = {RequestMethod.POST})
    public CommonReturnType publishPromo(@Param(value="id") Integer id, @Param(value="token") String token) throws BusinessException {
        //校验登录态
        UserModel userModel = userService.validateToken(token);
        if(userModel == null){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN,"用户未登录");
        }
        promoService.publicPromo(id);
        return CommonReturnType.create("发布活动成功");
    }

    @RequestMapping(value = "/select", method = {RequestMethod.POST})
    public CommonReturnType selectPromo(@Param(value="itemId") Integer itemId, @Param(value="token") String token) throws BusinessException {
        //校验登录态
        UserModel userModel = userService.validateToken(token);
        if(userModel == null){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN,"用户未登录");
        }
        PromoModel promoModel = promoService.getPromoByItemId(itemId);
        return CommonReturnType.create(promoModel);
    }

    @RequestMapping(value = "/selectByName", method = {RequestMethod.POST})
    public CommonReturnType selectPromo(@Param(value="name") String name, @Param(value="token") String token) throws BusinessException {
        System.out.println(name + " " + token);
        //校验登录态
        UserModel userModel = userService.validateToken(token);
        if(userModel == null){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN,"用户未登录");
        }
        List<PromoModel> list = promoService.selectByName(name);
        return CommonReturnType.create(list);
    }


}
