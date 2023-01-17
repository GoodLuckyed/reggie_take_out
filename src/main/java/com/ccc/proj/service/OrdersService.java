package com.ccc.proj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ccc.proj.entity.Orders;

public interface OrdersService extends IService<Orders> {

    /**
     * 用户下单
     * @param orders
     */
    void submit(Orders orders);
}
