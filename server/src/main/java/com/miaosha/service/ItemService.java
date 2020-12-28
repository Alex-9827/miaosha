package com.miaosha.service;

import com.miaosha.dataobject.ItemStockDO;
import com.miaosha.dataobject.StockLogDO;
import com.miaosha.service.model.ItemModel;

import java.util.List;

public interface ItemService {
    boolean decreaseStock(Integer itemId, Integer amount);
    boolean asyncDecreaseStock(Integer itemId, Integer amount);
    boolean increaseSales(Integer itemId, Integer amount);

    String initStockLog(Integer itemId, Integer amount);
    boolean updateStockLog(StockLogDO stockLogDO);
    StockLogDO getStockLogById(String stockLogId);

    Integer getItemStockById(Integer itemId);
    Double getItemPriceById(Integer itemId);

    ItemModel getItemById(Integer id);

    List<ItemModel> getItems();
    boolean deleteById(Integer id);

    boolean update(ItemModel itemModel);

    boolean insert(ItemModel itemModel);

    List<ItemModel> selectItemByName(String name);
}
