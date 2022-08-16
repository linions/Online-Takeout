package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.R;
import com.reggie.dto.DishDto;
import com.reggie.dto.SetmealDto;
import com.reggie.pojo.Category;
import com.reggie.pojo.Dish;
import com.reggie.pojo.Setmeal;
import com.reggie.service.CategoryService;
import com.reggie.service.SetmealDishService;
import com.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequestMapping("/setmeal")
@RestController
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;


    /*新增套餐*/
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("套餐信息：{}",setmealDto);
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功！");
    }

    /*套餐的分页查询*/
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
//        构造分页构造器对象
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();
//        条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
//        添加查询条件
        queryWrapper.like(name != null, Setmeal::getName, name);
//        添加排序条件
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
//        执行分页查询
        setmealService.page(pageInfo, queryWrapper);

//        对象拷贝
        BeanUtils.copyProperties(pageInfo, setmealDtoPage, "records");
        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
//            对象拷贝
            BeanUtils.copyProperties(item, setmealDto);
//            分类id
            Long categoryId = item.getCategoryId();
//            根据分类id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category!=null){
//                分类名称
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        setmealDtoPage.setRecords(list);
        return R.success(setmealDtoPage);

    }

    @DeleteMapping
    /*删除套餐同时删除与套餐关联的菜品数据*/
    public R<String> remove(@RequestParam List<Long> ids){
        log.info("ids：{}",ids);
        setmealService.removeWithDish(ids);
        return R.success("套餐删除成功！");
    }

    /*根据条件查询套餐数据*/
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }

    //    查询菜品信息
    @GetMapping("/{id}")
    public R<SetmealDto> update(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.getBySetmealIdWithDish(id);
        return R.success(setmealDto);
    }

    @PutMapping
    /*修改套餐同时修改与套餐关联的菜品数据*/
    public R<String> update(@RequestBody SetmealDto setmealDto){
        log.info(setmealDto.toString());
        setmealService.updateWithDish(setmealDto);
        return R.success("修改套餐成功！");
    }

    @PostMapping("/status/{status}")
    /*修改套餐售卖状态*/
    public R<String> updateStatus(@PathVariable int status,@RequestParam List<Long> ids){
        log.info("status：{}，ids:{}",status,ids);
        setmealService.updateStatus(status,ids);
        return R.success("修改套餐售卖状态成功！");
    }

}
