package com.miaosha.service.impl;

import com.miaosha.dao.PromoDOMapper;
import com.miaosha.dataobject.ItemDO;
import com.miaosha.dataobject.ItemStockDO;
import com.miaosha.dataobject.PromoDO;
import com.miaosha.service.ItemService;
import com.miaosha.service.PromoService;
import com.miaosha.service.model.ItemModel;
import com.miaosha.service.model.PromoModel;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class PromoServiceImpl implements PromoService {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ItemService itemService;

    @Autowired
    private PromoDOMapper promoDOMapper;

    @Override
    public void publicPromo(Integer promoId) {
        //缓存活动信息
        PromoModel promoModel = this.getPromoById(promoId);
        redisTemplate.opsForValue().set("promo_model_"+promoId, promoModel);

        Integer itemId = promoModel.getItemId();
        Integer stock = itemService.getItemStockById(itemId);
        //将数据库数据同步至缓存
        redisTemplate.opsForValue().set("item_stock_"+itemId, stock);
        redisTemplate.expire("item_stock_"+itemId, 30, TimeUnit.MINUTES);

        //秒杀大闸设置为5倍库存
        redisTemplate.opsForValue().set("item_secKillToken_"+itemId, stock * 5);
        redisTemplate.expire("item_secKillToken_"+itemId, 30, TimeUnit.MINUTES);

        System.out.println("Stock form MySql to Redis: " + stock);
    }

    @Override
    public String generateSeckillToken(Integer promoId, Integer itemId, Integer userId) {

        //库存耗尽直接返回null,不必苛求，后期减库存时会再次校验库存是否充足
        if(redisTemplate.hasKey("item_stock_empty_"+itemId)){
            return null;
        }

        //秒杀大闸
        long result = redisTemplate.opsForValue().increment("item_secKillToken_"+itemId, -1);
        if(result < 0){
            return null;
        }

        //验证活动信息
        PromoModel promoModel = (PromoModel) redisTemplate.opsForValue().get("promo_model_"+promoId);
        if(promoModel == null){
            return null;
        }
        if(promoModel.getItemId() != itemId){
            return null;
        }
        if(promoModel.getStartDate().isAfterNow() || promoModel.getEndDate().isBeforeNow()){
            return null;
        }

        //生成秒杀令牌，并设置5分钟有效期
        String secKillToken = UUID.randomUUID().toString().replace("-","");
        redisTemplate.opsForValue().set(secKillToken, "item_"+itemId+"_user_"+userId);
        redisTemplate.expire(secKillToken, 5, TimeUnit.MINUTES);

        return secKillToken;
    }

    @Override
    public PromoModel getPromoById(Integer id) {
        PromoDO promoDO = promoDOMapper.selectByPrimaryKey(id);
        PromoModel promoModel = convertFromDataObject(promoDO);
        return promoModel;
    }

    @Override
    public boolean validateSecKillToken(Integer itemId, Integer userId, String secKillToken) {
        String valueInCache = (String)redisTemplate.opsForValue().get(secKillToken);
        String value = "item_"+itemId+"_user_"+userId;
        if(value.equals(valueInCache) == false){
            return false;
        }
        return true;
    }

    @Override
    public PromoModel getPromoByItemId(Integer itemId) {
        PromoDO promoDO = promoDOMapper.selectByItemId(itemId);
        System.out.println("itemId: "+ itemId);
        System.out.println(promoDO);
        PromoModel promoModel = convertFromDataObject(promoDO);
        return promoModel;
    }

    @Override
    public List<PromoModel> selectByName(String name) {
        List<PromoModel> list = new ArrayList<>();
        List<PromoDO> l = promoDOMapper.selectByName(name);
        for(PromoDO promoDO : l){
            list.add(convertFromDataObject(promoDO));
        }
        return list;
    }

    private PromoModel convertFromDataObject(PromoDO promoDO){
        if(promoDO == null){
            return null;
        }
        PromoModel promoModel = new PromoModel();
        BeanUtils.copyProperties(promoDO,promoModel);
        promoModel.setPromoItemPrice(new BigDecimal(promoDO.getPromoItemPrice()));
        promoModel.setStartDate(new DateTime(promoDO.getStartDate()));
        promoModel.setEndDate(new DateTime(promoDO.getEndDate()));
        return promoModel;
    }
}
