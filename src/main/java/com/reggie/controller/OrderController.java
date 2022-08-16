package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.BaseContext;
import com.reggie.common.R;
import com.reggie.dto.DishDto;
import com.reggie.dto.OrdersDto;
import com.reggie.dto.SetmealDto;
import com.reggie.pojo.*;
import com.reggie.service.OrderDetailService;
import com.reggie.service.OrderService;
import com.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.tomcat.jni.OS;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@RequestMapping("/order")
@RestController
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderDetailService orderDetailService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        log.info("提交订单数据：{}", orders);
        orderService.submit(orders);
        return R.success("下单成功！");
    }

    /*订单信息的分页查询*/
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String number, String beginTime, String endTime) {
//        构造分页构造器对象
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>();
//        条件构造器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
//        添加查询条件
        queryWrapper.like(number != null, Orders::getNumber, number);
        queryWrapper.between(beginTime != null && endTime != null, Orders::getCheckoutTime,beginTime, endTime);
//        添加排序条件
        queryWrapper.orderByDesc(Orders::getOrderTime);
//        执行分页查询
        orderService.page(pageInfo, queryWrapper);

//        对象拷贝
        BeanUtils.copyProperties(pageInfo, ordersDtoPage, "records");
        List<Orders> records = pageInfo.getRecords();

        List<OrdersDto> list = records.stream().map((item) -> {
            OrdersDto ordersDto = new OrdersDto();
//            对象拷贝
            BeanUtils.copyProperties(item, ordersDto);
//            分类id
            Long userId = item.getUserId();
//            根据分类id查询分类对象
            User user = userService.getById(userId);
            if (userId != null) {
//                分类名称
//                String userName = user.getName();
                String userName=String.valueOf(user.getPhone());
                ordersDto.setUserName(userName);
            }
            return ordersDto;
        }).collect(Collectors.toList());

        ordersDtoPage.setRecords(list);
        return R.success(ordersDtoPage);
    }

    @PutMapping
    public R<String> update(@RequestBody Orders orders) {
        log.info("orders:{}", orders);
//        查询该订单信息
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getId, orders.getId());
        Orders order = orderService.getOne(queryWrapper);
//        更新状态状态信息
        order.setStatus(orders.getStatus());
        orderService.updateById(order);
        return R.success("修改订单信息成功！");
    }

    @GetMapping("/userPage")
    public R<Page> userPage(int page, int pageSize) {
        //        构造分页构造器对象
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>();

//        构造查询条件
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId,BaseContext.getCurrentId());
//        添加排序条件
        queryWrapper.orderByAsc(Orders::getOrderTime).orderByDesc(Orders::getCheckoutTime);

        orderService.page(pageInfo, queryWrapper);
        BeanUtils.copyProperties(pageInfo, ordersDtoPage, "records");
        List<Orders> records = pageInfo.getRecords();

        List<OrdersDto> ordersDtoList = records.stream().map((item) -> {
            OrdersDto ordersDto= new OrdersDto();
            BeanUtils.copyProperties(item, ordersDto);
            Long orderId = item.getId();
            LambdaQueryWrapper<OrderDetail> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(orderId!=null,OrderDetail::getOrderId,orderId);
            List<OrderDetail> orderDetailList = orderDetailService.list(lambdaQueryWrapper);
            ordersDto.setOrderDetails(orderDetailList);
            return ordersDto;
        }).collect(Collectors.toList());

        ordersDtoPage.setRecords(ordersDtoList);
        return R.success(ordersDtoPage);
    }
}
