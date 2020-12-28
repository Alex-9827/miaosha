package com.miaosha.service.model;



import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class ItemModel implements Serializable {
    private Integer id;
    private String title;
    private BigDecimal price;
    private Integer stock;
    private Integer sales;
    private String description;
    private String imgUrl;
    private PromoModel promoModel;
}
