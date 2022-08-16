package com.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.mapper.DishFalvorMapper;
import com.reggie.mapper.DishMapper;
import com.reggie.pojo.Dish;
import com.reggie.pojo.DishFlavor;
import com.reggie.service.DishFlavorService;
import com.reggie.service.DishService;
import org.springframework.stereotype.Service;

@Service
public class DishFalvorServiceImpl extends ServiceImpl<DishFalvorMapper, DishFlavor>  implements DishFlavorService {
}
