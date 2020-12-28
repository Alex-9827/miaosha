package com.miaosha.service;

import com.miaosha.service.model.OrderModel;

import java.util.List;

public interface OrderService {
    boolean createOrder(Integer userId, Integer itemId, Integer promoId, Integer amount, String stockLogId);
    List<OrderModel> getOrderByUid(Integer uid);
    List<OrderModel> selectByName(String name);
}
