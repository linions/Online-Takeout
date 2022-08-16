package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.CustomException;
import com.reggie.dto.SetmealDto;
import com.reggie.mapper.SetmealMapper;
import com.reggie.pojo.Setmeal;
import com.reggie.pojo.SetmealDish;
import com.reggie.service.SetmealDishService;
import com.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal>  implements SetmealService {


    @Autowired
    private SetmealDishService setmealDishService;


    /*新增菜品套餐，同时添加套餐信息和菜品关联信息*/
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
//        保存套餐的基本信息，操作setmeal表，执行inssert操作
        this.save(setmealDto);

//        保存套餐和菜品关系表信息，操作setmeal_dish表
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

//        保存套餐和菜品的关联信息，操作setmeal_dish，执行insert操作
        setmealDishService.saveBatch(setmealDishes);
    }

    /*删除套餐同时删除与套餐关联的菜品数据*/
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
//        查询套餐状态，确定是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);

        int count = this.count(queryWrapper);
        if (count > 0) {
            //        如果不可以删除，就抛出一个业务异常
            throw new CustomException("套餐正在售卖中，不能删除。");
        }

//        如果可以删除，就先删除套餐表中的数据
        this.removeByIds(ids);
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        //        删除关系表中的数据
        setmealDishService.remove(lambdaQueryWrapper);

    }

    //    根据套餐id查询套餐及其菜品信息
    @Override
    public SetmealDto getBySetmealIdWithDish(Long id) {
        //        查询套餐的基本信息，从setMeal表中进行查询
        Setmeal setmeal = this.getById(id);

        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);

//        查询当前套餐对应的菜品信息，从setMeal_dish中查询
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmeal.getId());
        List<SetmealDish> setmealDishes = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(setmealDishes);

        return setmealDto;
    }

    /*修改套餐同时修改与套餐关联的菜品数据*/
    @Override
    @Transactional
    public void updateWithDish(SetmealDto setmealDto) {
//        更新套餐的基本信息到菜品表setMeal中
        this.updateById(setmealDto);

//        清理当前套餐对应的菜品信息
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(queryWrapper);

//        重新添加新的菜品信息
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

//        保存套餐的菜品数据到setMeal_dish表中
        setmealDishService.saveBatch(setmealDishes);
    }

    /*修改套餐售卖状态数据*/
    @Override
    public void updateStatus(int status, List<Long> ids) {
//        便利每个setMealId修改其状态
        for (Long id : ids) {
            Setmeal setmeal = this.getById(id);
            setmeal.setStatus(status);
            this.updateById(setmeal);
        }
    }

}
