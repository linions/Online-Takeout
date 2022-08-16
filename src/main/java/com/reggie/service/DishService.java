package com.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.dto.DishDto;
import com.reggie.pojo.Category;
import com.reggie.pojo.Dish;

import java.util.List;


public interface DishService extends IService<Dish> {
//    新增菜品，同时插入菜品对应的口味数据，需要同时对两张表进行操作：dish、dish_flavor
    public void saveWithFlavor(DishDto dishDto);

//    根据菜品的id来查询菜品信息及其口味信息
    public DishDto getByIdWithFlavor(Long id);

    //    更新菜品信息，同时更新菜品对应的口味数据，需要同时对两张表进行操作：dish、dish_flavor
    public void updateWithFlavor(DishDto dishDto);

    /*删除菜品同时删除与菜品关联的口味数据*/
    public void removeWithFlavor(List<Long> ids);

    /*修改菜品售卖状态数据*/
    void updateStatus(int status, List<Long> ids);
}
