package com.miaosha.service;

import com.miaosha.service.model.PromoModel;

import java.util.List;

public interface PromoService {
    void publicPromo(Integer promoId);
    String generateSeckillToken(Integer promoId, Integer itemId, Integer userId);
    PromoModel getPromoById(Integer id);
    boolean validateSecKillToken(Integer itemId, Integer userId, String secKillToken);
    PromoModel getPromoByItemId(Integer itemId);
    List<PromoModel> selectByName(String name);
}
