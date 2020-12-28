package com.miaosha.service.impl;

import com.miaosha.dao.OrderDOMapper;
import com.miaosha.dataobject.OrderDO;
import com.miaosha.dataobject.PromoDO;
import com.miaosha.dataobject.StockLogDO;
import com.miaosha.service.ItemService;
import com.miaosha.service.OrderService;
import com.miaosha.service.SequenceService;
import com.miaosha.service.model.OrderModel;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private ItemService itemService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SequenceService sequenceService;

    @Autowired
    private OrderDOMapper orderDOMapper;

    @Override
    @Transactional
    public boolean createOrder(Integer userId, Integer itemId, Integer promoId, Integer amount, String stcokLogId){

        //Redis减库存
        Boolean result = itemService.decreaseStock(itemId, amount);
        if(result == false){
            return false;
        }




        Integer stock = (Integer)redisTemplate.opsForValue().get("item_stock_" + itemId);
        System.out.println("Stock in Redis After Decrease: " + stock);



        //订单生成
        //生成订单号，获取商品详情
        String orderId = generateOrderNO();
        Double itemPrice = itemService.getItemPriceById(itemId);

        //订单入库
        OrderDO orderDO = new OrderDO();
        orderDO.setId(orderId);
        orderDO.setUserId(userId);
        orderDO.setItemId(itemId);
        orderDO.setItemPrice(itemPrice);
        orderDO.setAmount(amount);
        orderDO.setPromoId(promoId);
        orderDO.setOrderPrice(itemPrice * amount);

        orderDOMapper.insertSelective(orderDO);


        //更新销量
        itemService.increaseSales(itemId, amount);


        //获取库存流水
        StockLogDO stockLogDO = itemService.getStockLogById(stcokLogId);
        if(stockLogDO == null){
            return false;
        }

        //订单创建成功，更新库存流水状态。
        //2代表订单生成成功
        stockLogDO.setStatus(2);
        itemService.updateStockLog(stockLogDO);
        return true;
    }

    @Override
    public List<OrderModel> getOrderByUid(Integer userId) {
        List<OrderDO> l = orderDOMapper.selectByUid(userId);
        System.out.println(l.size());
        List<OrderModel> list = new ArrayList<>();
        for(OrderDO orderDO : l){
            list.add(convertFromOrderDO(orderDO));
        }
        return list;
    }

    @Override
    public List<OrderModel> selectByName(String name) {
        List<OrderDO> l = orderDOMapper.selectByName(name);
        List<OrderModel> list = new ArrayList<>();
        for(OrderDO orderDO : l){
            list.add(convertFromOrderDO(orderDO));
        }
        return list;
    }

    @Transactional
    public String generateOrderNO(){
        StringBuilder stringBuilder = new StringBuilder();

        //前8位为日期
        String now = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE).replace("-","");
        stringBuilder.append(now);

        //中间6位为自增序列
        String sequence = sequenceService.offerSequenceByName("order_info").toString();
        for(int i = 0; i < 6-sequence.length(); i ++) stringBuilder.append(0);
        stringBuilder.append(sequence);

        //最后2位为分库分表位，暂时写死
        stringBuilder.append("00");
        return stringBuilder.toString();
    }
    private OrderModel convertFromOrderDO(OrderDO orderDO){
        if(orderDO == null){
            return null;
        }
        OrderModel orderModel = new OrderModel();
        orderModel.setId(orderDO.getId());
        orderModel.setAmount(orderDO.getAmount());
        orderModel.setItemPrice(new BigDecimal(orderDO.getItemPrice()));
        orderModel.setOrderPrice(new BigDecimal(orderDO.getOrderPrice()));
        orderModel.setItemId(orderDO.getItemId());
        orderModel.setUserId(orderDO.getUserId());
        orderModel.setPromoId(orderDO.getPromoId());
        return orderModel;
    }

}
