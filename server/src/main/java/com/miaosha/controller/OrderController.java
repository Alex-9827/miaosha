package com.miaosha.controller;

import com.miaosha.error.BusinessException;
import com.miaosha.error.EmBusinessError;
import com.miaosha.mq.MqProducer;
import com.miaosha.response.CommonReturnType;
import com.miaosha.service.ItemService;
import com.miaosha.service.OrderService;
import com.miaosha.service.PromoService;
import com.miaosha.service.UserService;
import com.miaosha.service.model.OrderModel;
import com.miaosha.service.model.UserModel;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.*;

@Controller
@ResponseBody
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private MqProducer mqProducer;

    private ExecutorService executorService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private PromoService promoService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    @PostConstruct
    public void init(){
        executorService = Executors.newFixedThreadPool(20);
    }

    //获取秒杀令牌
    @RequestMapping(value = "/secKillToken", method = {RequestMethod.POST})
    public CommonReturnType generateSecKillToken(@RequestParam(name="itemId") Integer itemId,
                                       @RequestParam(name="promoId") Integer promoId,
                                       @RequestParam(name="token") String token) throws BusinessException {
        System.out.println("secKillToken");
        //校验登录态
        UserModel userModel = userService.validateToken(token);
        if(userModel == null){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN,"用户未登录");
        }

        //获取秒杀令牌
        Integer userId = userModel.getId();
        String secKillToken = promoService.generateSeckillToken(promoId, itemId, userId);
        if(secKillToken == null){
            throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH,"秒杀令牌获取失败");
        }
        return CommonReturnType.create(secKillToken);
    }


    //下单
    @RequestMapping("/createOrder")
    public CommonReturnType createOrder(@RequestParam(name = "itemId") Integer itemId,
                              @RequestParam(name = "amount") Integer amount,
                              @RequestParam(name = "promoId") Integer promoId,
                              @RequestParam(name = "secKillToken") String secKillToken,
                              @RequestParam(name = "token") String token) throws BusinessException {
        System.out.println(secKillToken);
        //验证登录态
        UserModel userModel = userService.validateToken(token);
        if(userModel == null){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN,"用户未登录");
        }
        Integer userId = userModel.getId();
        //验证秒杀令牌
        boolean result = promoService.validateSecKillToken(itemId, userId, secKillToken);
        if(result == false){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"秒杀令牌校验失败");
        }

        //拥塞窗口为20的等待队列，用来队列化泄洪
        Future<Object> future = executorService.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                String stockLogId = itemService.initStockLog(itemId, amount);
                if(mqProducer.transactionAsyncReduceStock(itemId, amount, promoId, userId, stockLogId) == false){
                    throw new BusinessException(EmBusinessError.UNKNOWN_ERROR,"下单失败");
                }
                return null;
            }
        });
        try{
            future.get();
        }catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        return CommonReturnType.create("下单成功");
    }


    @RequestMapping(value = "/get", method = {RequestMethod.POST})
    public CommonReturnType getOrder(@RequestParam(name="token") String token) throws BusinessException {

        //校验登录态
        UserModel userModel = userService.validateToken(token);
        if(userModel == null){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN,"用户未登录");
        }
        List<OrderModel> list = orderService.getOrderByUid(userModel.getId());

        return CommonReturnType.create(list);
    }

    @RequestMapping(value = "/selectByName", method = {RequestMethod.POST})
    public CommonReturnType selectOrder(@Param(value="name") String name, @Param(value="token") String token) throws BusinessException {
        System.out.println(name + " " + token);
        //校验登录态
        UserModel userModel = userService.validateToken(token);
        if(userModel == null){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN,"用户未登录");
        }
        List<OrderModel> list = orderService.selectByName(name);
        return CommonReturnType.create(list);
    }
}
