package com.miaosha.service.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class OrderModel implements Serializable{
    private String id;
    private Integer userId;
    private Integer itemId;
    private Integer promoId;
    private BigDecimal itemPrice;
    private Integer amount;
    private BigDecimal orderPrice;
}
