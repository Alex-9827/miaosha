package com.miaosha.service.impl;


import com.miaosha.dao.ItemDOMapper;
import com.miaosha.dao.ItemStockDOMapper;
import com.miaosha.dao.StockLogDOMapper;
import com.miaosha.dataobject.ItemDO;
import com.miaosha.dataobject.ItemStockDO;
import com.miaosha.dataobject.StockLogDO;
import com.miaosha.service.ItemService;
import com.miaosha.service.model.ItemModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ItemStockDOMapper itemStockDOMapper;

    @Autowired
    private ItemDOMapper itemDOMapper;

    @Autowired
    private StockLogDOMapper stockLogDOMapper;


    @Override
    @Transactional
    public boolean decreaseStock(Integer itemId, Integer amount) {
        long result = redisTemplate.opsForValue().increment("item_stock_"+itemId, amount.intValue()*-1);
        if(result > 0){
            return true;
        }
        else if(result == 0){
            return false;
        }
        else{
            redisTemplate.opsForValue().increment("item_stock_"+itemId, amount.intValue());
            redisTemplate.opsForValue().set("item_stock_empty_"+itemId, true);
            return false;
        }
    }

    @Override
    public boolean asyncDecreaseStock(Integer itemId, Integer amount) {
        Integer result = itemStockDOMapper.decreaseStock(itemId, amount);
        if(result >= 0) return true;
        else return false;
    }

    @Override
    public Integer getItemStockById(Integer itemId) {
        return itemStockDOMapper.getStockById(itemId);
    }

    @Override
    public String initStockLog(Integer itemId, Integer amount) {
        String stockLogId = UUID.randomUUID().toString().replace("-","");
        StockLogDO stockLogDO = new StockLogDO();
        stockLogDO.setItemId(itemId);
        stockLogDO.setAmount(amount);
        stockLogDO.setStockLogId(stockLogId);
        stockLogDO.setStatus(1);
        stockLogDOMapper.insertSelective(stockLogDO);
        return stockLogId;
    }

    @Override
    public StockLogDO getStockLogById(String stockLogId) {
        return stockLogDOMapper.selectByPrimaryKey(stockLogId);
    }

    @Override
    public boolean updateStockLog(StockLogDO stockLogDO) {
        stockLogDOMapper.updateByPrimaryKeySelective(stockLogDO);
        return true;
    }

    @Override
    public Double getItemPriceById(Integer itemId) {
        ItemDO itemDO = itemDOMapper.selectByPrimaryKey(itemId);
        return itemDO.getPrice();
    }

    @Override
    public boolean increaseSales(Integer itemId, Integer amount) {
        itemDOMapper.increaseSales(itemId, amount);
        return true;
    }



    @Override
    public ItemModel getItemById(Integer id) {
        ItemDO itemDO = itemDOMapper.selectByPrimaryKey(id);
        ItemStockDO itemStockDO = itemStockDOMapper.selectByPrimaryKey(id);
        ItemModel itemModel = convertModelFromDataObject(itemDO, itemStockDO);
        return itemModel;
    }

    @Override
    public List<ItemModel> getItems() {
        List<ItemDO> doList = itemDOMapper.selectAll();
        List<ItemModel> list = new ArrayList<>();
        for(ItemDO itemDO : doList){
            ItemStockDO itemStockDO = itemStockDOMapper.selectByPrimaryKey(itemDO.getId());
            list.add(convertModelFromDataObject(itemDO, itemStockDO));
        }
        return list;
    }

    @Override
    public boolean deleteById(Integer id) {
        itemStockDOMapper.deleteByPrimaryKey(id);
        itemDOMapper.deleteByPrimaryKey(id);
        return true;
    }

    @Override
    public boolean update(ItemModel itemModel) {
        ItemDO itemDO = convertItemDOFromItemModel(itemModel);
        ItemStockDO itemStockDO = convertItemStockDOFromItemModel(itemModel);
        itemDOMapper.updateByPrimaryKeySelective(itemDO);
        itemStockDOMapper.updateByPrimaryKeySelective(itemStockDO);
        return true;
    }

    @Override
    public boolean insert(ItemModel itemModel) {
        ItemDO itemDO = convertItemDOFromItemModel(itemModel);
        System.out.println("DOID: " + itemDO.getId());
        itemDOMapper.insertSelective(itemDO);
        System.out.println("DOID: " + itemDO.getId());
        itemModel.setId(itemDO.getId());
        ItemStockDO itemStockDO = convertItemStockDOFromItemModel(itemModel);

        itemStockDOMapper.insertSelective(itemStockDO);
        return true;
    }

    @Override
    public List<ItemModel> selectItemByName(String name) {
        List<ItemDO> doList = itemDOMapper.selectByName(name);
        List<ItemModel> list = new ArrayList<>();
        for(ItemDO itemDO : doList){
            ItemStockDO itemStockDO = itemStockDOMapper.selectByPrimaryKey(itemDO.getId());
            list.add(convertModelFromDataObject(itemDO, itemStockDO));
        }
        return list;
    }

    private ItemModel convertModelFromDataObject(ItemDO itemDO, ItemStockDO itemStockDO){
        if(itemDO == null || itemStockDO == null) return null;
        ItemModel itemModel = new ItemModel();
        BeanUtils.copyProperties(itemDO,itemModel);
        itemModel.setPrice(new BigDecimal(itemDO.getPrice()));
        itemModel.setStock(itemStockDO.getStock());

        return itemModel;
    }

    private ItemDO convertItemDOFromItemModel(ItemModel itemModel){
        if(itemModel == null){
            return null;
        }
        ItemDO itemDO = new ItemDO();
        BeanUtils.copyProperties(itemModel,itemDO);
        itemDO.setPrice(itemModel.getPrice().doubleValue());
        return itemDO;
    }
    private ItemStockDO convertItemStockDOFromItemModel(ItemModel itemModel){
        if(itemModel == null){
            return null;
        }
        ItemStockDO itemStockDO = new ItemStockDO();
        itemStockDO.setItemId(itemModel.getId());
        itemStockDO.setStock(itemModel.getStock());
        return itemStockDO;
    }




}
