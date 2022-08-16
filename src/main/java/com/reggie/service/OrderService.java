package com.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.pojo.Orders;
import com.reggie.pojo.User;
import com.sun.org.apache.xpath.internal.operations.Or;


public interface OrderService extends IService<Orders> {

    /*用户下单*/
    public void submit(Orders orders);
}
