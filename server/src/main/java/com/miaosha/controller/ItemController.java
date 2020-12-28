package com.miaosha.controller;

import com.miaosha.error.BusinessException;
import com.miaosha.error.EmBusinessError;
import com.miaosha.response.CommonReturnType;
import com.miaosha.service.ItemService;
import com.miaosha.service.UserService;
import com.miaosha.service.model.ItemModel;
import com.miaosha.service.model.UserModel;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.List;

@Controller
@ResponseBody
@RequestMapping("/item")
public class ItemController {

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @RequestMapping(value = "/items", method = {RequestMethod.POST})
    public CommonReturnType getItems(@Param(value="token") String token) throws BusinessException {
        //校验登录态
        UserModel userModel = userService.validateToken(token);
        if(userModel == null){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN,"用户未登录");
        }
        List<ItemModel> list = itemService.getItems();
        return CommonReturnType.create(list);
    }

    @RequestMapping(value = "/delete", method = {RequestMethod.POST})
    public CommonReturnType deleteItem(@Param(value="id") Integer id, @Param(value="token") String token) throws BusinessException {
        System.out.println(id + " " + token);
        //校验登录态
        UserModel userModel = userService.validateToken(token);
        if(userModel == null){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN,"用户未登录");
        }
        itemService.deleteById(id);
        return CommonReturnType.create("刪除成功");
    }

    @RequestMapping(value = "/get", method = {RequestMethod.POST})
    public CommonReturnType getItem(@Param(value="id") Integer id, @Param(value="token") String token) throws BusinessException {
        System.out.println(id + " " + token);
        //校验登录态
        UserModel userModel = userService.validateToken(token);
        if(userModel == null){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN,"用户未登录");
        }
        ItemModel itemModel = itemService.getItemById(id);
        return CommonReturnType.create(itemModel);
    }


    @RequestMapping(value = "/select", method = {RequestMethod.POST})
    public CommonReturnType selectItem(@Param(value="name") String name, @Param(value="token") String token) throws BusinessException {
        System.out.println(name + " " + token);
        //校验登录态
        UserModel userModel = userService.validateToken(token);
        if(userModel == null){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN,"用户未登录");
        }
        List<ItemModel> list = itemService.selectItemByName(name);
        return CommonReturnType.create(list);
    }

    @RequestMapping(value = "/add", method = {RequestMethod.POST})
    public CommonReturnType addItem(@Param(value="title") String title,
                                       @Param(value="price") BigDecimal price,
                                       @Param(value="stock") Integer stock,
                                       @Param(value="sales") Integer sales,
                                       @Param(value="description") String description,
                                       @Param(value="imgUrl") String imgUrl,
                                       @Param(value="token") String token) throws BusinessException {
        //校验登录态
        UserModel userModel = userService.validateToken(token);
        if(userModel == null){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN,"用户未登录");
        }
        System.out.println(imgUrl);

        ItemModel itemModel = new ItemModel();
        itemModel.setTitle(title);
        itemModel.setPrice(price);
        itemModel.setStock(stock);
        itemModel.setSales(sales);
        itemModel.setDescription(description);
        itemModel.setImgUrl(imgUrl);
        itemService.insert(itemModel);
        return CommonReturnType.create("添加成功");
    }


    @RequestMapping(value = "/update", method = {RequestMethod.POST})
    public CommonReturnType updateItem(@Param(value="id") Integer id,
                                       @Param(value="title") String title,
                                       @Param(value="price") BigDecimal price,
                                       @Param(value="stock") Integer stock,
                                       @Param(value="sales") Integer sales,
                                       @Param(value="description") String description,
                                       @Param(value="token") String token) throws BusinessException {
        //校验登录态
        UserModel userModel = userService.validateToken(token);
        if(userModel == null){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN,"用户未登录");
        }
        ItemModel itemModel = new ItemModel();
        itemModel.setId(id);
        itemModel.setTitle(title);
        itemModel.setPrice(price);
        itemModel.setStock(stock);
        itemModel.setSales(sales);
        itemModel.setDescription(description);
        itemService.update(itemModel);
        return CommonReturnType.create("更新成功");
    }


}
