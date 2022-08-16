package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.R;
import com.reggie.pojo.Category;
import com.reggie.pojo.Employee;
import com.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    /*新增分类*/
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("category：{}",category);
        categoryService.save(category);
        return R.success("新增分类成功。");
    }

    /*分类的分页查询*/
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize) {
        log.info("page = {}，pageSize = {}", page, pageSize);
//        构造分页构造器
        Page pageInfo = new Page(page, pageSize);

//        构造条件查询构造器哦
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper();
//        添加排序条件
        queryWrapper.orderByAsc(Category::getSort);
//        执行查询
        categoryService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    /*删除分类*/
    @DeleteMapping
    public R<String> delete(Long id){
        log.info("删除分类，id为：{}",id);
        categoryService.remove(id);
        return R.success("分类信息删除成功！");
    }

//    根据id修改分类信息
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("修改分类信息：{}",category);
        categoryService.updateById(category);
        return R.success("修改分类信息成功！");
    }

    /*根据菜品分类进行查询*/
    @GetMapping("/list")
    public R<List<Category>> list(Category category) {
//        条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
//        添加条件
        queryWrapper.eq(category.getType() != null,Category::getType,category.getType());
//        添加排序条件
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }

}
