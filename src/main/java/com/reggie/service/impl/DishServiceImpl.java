package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.CustomException;
import com.reggie.dto.DishDto;
import com.reggie.mapper.DishFalvorMapper;
import com.reggie.mapper.DishMapper;
import com.reggie.mapper.EmployeeMapper;
import com.reggie.pojo.*;
import com.reggie.service.DishFlavorService;
import com.reggie.service.DishService;
import com.reggie.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish>  implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    /*新增菜品，同时保存对应的口味数据*/
    @Transactional
    @Override
    public void updateWithFlavor(DishDto dishDto) {
//        更新菜品的基本信息到菜品表dish中
        this.updateById(dishDto);

//        清理当前菜品对应的口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);
//        重新添加新的菜品口味信息
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

//        保存菜品的口味数据到菜品口味表dish_flavors
        dishFlavorService.saveBatch(flavors);
    }

    /*删除菜品同时删除与菜品关联的口味数据*/
    @Override
    @Transactional
    public void removeWithFlavor(List<Long> ids) {
        //        查询菜品状态，确定是否可以删除
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId,ids);
        queryWrapper.eq(Dish::getStatus,1);

        int count = this.count(queryWrapper);
        if (count > 0) {
            //        如果不可以删除，就抛出一个业务异常
            throw new CustomException("菜品正在售卖中，不能删除。");
        }

//        如果可以删除，就先删除菜品表中的数据
        this.removeByIds(ids);
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(DishFlavor::getDishId,ids);
        //        删除关系表中的数据
        dishFlavorService.remove(lambdaQueryWrapper);
    }

    /*修改菜品售卖状态数据*/
    @Override
    public void updateStatus(int status, List<Long> ids) {
        //        便利每个DishId修改其状态
        for (Long id : ids) {
            Dish dish = this.getById(id);
            dish.setStatus(status);
            this.updateById(dish);
        }
    }

    //    根据菜品的id来查询菜品信息及其口味信息
    @Override
    public DishDto getByIdWithFlavor(Long id) {
//        查询菜品的基本信息，从dish表中进行查询
        Dish dish = this.getById(id);

        DishDto dishDto =new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

//        查询当前菜品对应的口味信息，从dish_flavor中查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);

        return dishDto;
    }


    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //        更新dish表信息
        this.save(dishDto);

//        菜品id
        Long dishId = dishDto.getId();

//        菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

//        保存菜品的口味数据到菜品口味表dish_flavors
        dishFlavorService.saveBatch(flavors);
    }
}
