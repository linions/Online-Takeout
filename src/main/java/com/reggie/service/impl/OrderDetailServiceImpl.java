package com.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.mapper.OrderDetailMapper;
import com.reggie.mapper.OrderMapper;
import com.reggie.pojo.OrderDetail;
import com.reggie.pojo.Orders;
import com.reggie.service.OrderDetailService;
import com.reggie.service.OrderService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail>  implements OrderDetailService {

}
