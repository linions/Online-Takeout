package com.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.mapper.ShoppingCartMapper;
import com.reggie.mapper.UserMapper;
import com.reggie.pojo.ShoppingCart;
import com.reggie.pojo.User;
import com.reggie.service.ShoppingCartService;
import com.reggie.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart>  implements ShoppingCartService {

}
