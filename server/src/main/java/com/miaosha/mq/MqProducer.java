package com.miaosha.mq;

import com.alibaba.fastjson.JSON;
import com.miaosha.dataobject.StockLogDO;
import com.miaosha.service.ItemService;
import com.miaosha.service.OrderService;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.apache.tomcat.jni.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

@Component
public class MqProducer {

//    private DefaultMQProducer producer;
    private TransactionMQProducer transactionMQProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ItemService itemService;

    @Autowired
    private OrderService orderService;


    @Value("${mq.nameserver.addr}")
    private String nameAddr;

    @Value("${mq.topicname}")
    private String topicName;

    @PostConstruct
    public void init() throws MQClientException {
        //MqProducer 初始化
        transactionMQProducer = new TransactionMQProducer("transaction_producer_group");
        transactionMQProducer.setNamesrvAddr(nameAddr);
        transactionMQProducer.start();

        transactionMQProducer.setTransactionListener(new TransactionListener() {
            @Override
            public LocalTransactionState executeLocalTransaction(Message message, Object o) {
                Map<String, Object> args = (Map)o;
                Integer itemId = (Integer)args.get("itemId");
                Integer amount = (Integer)args.get("amount");
                Integer promoId = (Integer)args.get("promoId");
                Integer userId = (Integer)args.get("userId");
                String stockLogId = (String)args.get("stockLogId");
                try{
                    orderService.createOrder(userId, itemId, promoId, amount, stockLogId);
                }
                catch (Exception e){
                    e.printStackTrace();
                    StockLogDO stockLogDO = itemService.getStockLogById(stockLogId);

                    //TODO
                    //创建订单失败，Redis是否更新不可知
                    //生成订单操作涉及数据库，将自动回滚
                    //根据库存流水信息，手动使Redis回滚

                    //3代表回滚
                    stockLogDO.setStatus(3);
                    itemService.updateStockLog(stockLogDO);
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                }
                return LocalTransactionState.COMMIT_MESSAGE;
            }

            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
                String jsonString = new String(messageExt.getBody());
                Map<String, Object> map = JSON.parseObject(jsonString, Map.class);
                String stockLogId = (String)map.get("stockLogId");
                StockLogDO stockLogDO = itemService.getStockLogById(stockLogId);
                if(stockLogDO == null){
                    return LocalTransactionState.UNKNOW;
                }
                else if(stockLogDO.getStatus() == 1){
                    return LocalTransactionState.UNKNOW;
                }
                else if(stockLogDO.getStatus() == 2){
                    return LocalTransactionState.COMMIT_MESSAGE;
                }
                else return LocalTransactionState.ROLLBACK_MESSAGE;
            }
        });

    }
    public boolean transactionAsyncReduceStock(Integer itemId, Integer amount, Integer promoId, Integer userId, String stockLogId) {
        Map<String, Object> map = new HashMap<>();
        map.put("itemId", itemId);
        map.put("amount", amount);
        map.put("stockLogId", stockLogId);

        Map<String, Object> args = new HashMap<>();
        args.put("itemId", itemId);
        args.put("amount", amount);
        args.put("promoId", promoId);
        args.put("userId", userId);
        args.put("stockLogId", stockLogId);

        Message message = new Message(topicName, "increase",
                JSON.toJSON(map).toString().getBytes(Charset.forName("UTF-8")));
        try{
            transactionMQProducer.sendMessageInTransaction(message, args);
        }
        catch (MQClientException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
