package com.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.dto.SetmealDto;
import com.reggie.pojo.Setmeal;

import java.util.List;


public interface SetmealService extends IService<Setmeal> {
    /*新增菜品套餐，同时添加套餐信息和菜品关联信息*/
    public void saveWithDish(SetmealDto setmealDto);

    /*删除套餐同时删除与套餐关联的菜品数据*/
    public void removeWithDish(List<Long> ids);

//    根据套餐id查询套餐及其菜品信息
    public SetmealDto getBySetmealIdWithDish(Long id);

    /*修改套餐同时修改与套餐关联的菜品数据*/
    void updateWithDish(SetmealDto setmealDto);

    /*修改套餐售卖状态数据*/
    void updateStatus(int status, List<Long> ids);
}
