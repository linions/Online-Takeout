package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.CustomException;
import com.reggie.mapper.CategoryMapper;
import com.reggie.pojo.Category;
import com.reggie.pojo.Dish;
import com.reggie.pojo.Setmeal;
import com.reggie.service.CategoryService;
import com.reggie.service.DishService;
import com.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    DishService dishService;

    @Autowired
    SetmealService setmealService;


    /*根据id删除分类，删除之前需要进行判断*/
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishqueryWrapper = new LambdaQueryWrapper<>();
        dishqueryWrapper.eq(Dish::getCategoryId,id);
        int count1 = dishService.count(dishqueryWrapper);
//        查询当前分类是否关联了菜品，如果已经关联，抛出一个业务异常
        if (count1 > 0){
        throw new CustomException("当前分类下关联了菜品，不能删除！");
        }
//        查询当前分类是否关联了分类，如果已经关联，抛出一个业务异常
        LambdaQueryWrapper<Setmeal> setmealQueryWrapper = new LambdaQueryWrapper<>();
        setmealQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2 = setmealService.count(setmealQueryWrapper);
        if (count2 > 0){
            throw new CustomException("当前分类下关联了套餐，不能删除！");
        }
//        正常删除
        super.removeById(id);
    }
}
