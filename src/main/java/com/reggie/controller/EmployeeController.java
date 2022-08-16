package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.BaseContext;
import com.reggie.common.R;
import com.reggie.mapper.EmployeeMapper;
import com.reggie.pojo.Employee;
import com.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

import static org.apache.commons.lang.StringUtils.*;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    EmployeeService employeeService;

    /**
     * 员工登录
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
//       1、 将页面提交的密码进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

//       2、根据用户命名查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

//        3、如果没有查询到结果表示没有该用户，所以登录失败
        if (emp == null) {
            return R.error("登录失败！");
        }

//        4、如果存在该用户，就比较密码是否正确，不正确也登录失败
        if (!emp.getPassword().equals(password)) {
            return R.error("登录失败！");
        }

//        5、查看员工的状态，如果为禁用状态，就返回员工已经禁用的结果
        if (emp.getStatus() == 0) {
            return R.error("账号已禁用！");
        }

//        6、登录成功，将员工id存入Session并返回登录成功的结果
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    /*
     *员工退出登录
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
//        清理Session中保存的当前员工的信息id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功！");
    }

    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("新增员工，员工信息：{}", employee.toString());
//        手动添加新增员工其他信息
//        设置初始密码为123456，并进行加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);
//         在数据库中添加记录
        employeeService.save(employee);
        return R.success("新增员工成功！");
    }

    /*员工信息的分页查询*/
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("page = {}，pageSize = {}，name = {}", page, pageSize, name);
//        构造分页构造器
        Page pageInfo = new Page(page, pageSize);

//        构造条件查询构造器哦
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
//        添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
//        添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

//        执行查询
        employeeService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    /*根据id修改员工信息*/
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        log.info(employee.toString());
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(empId);
        employeeService.updateById(employee);
        return R.success("员工信息修改成功！");
    }

    /*根据id查询员工信息*/
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        log.info("根据id查询员工信息");
        Employee employee = employeeService.getById(id);
        if (employee != null){
            return R.success(employee);
        }
        return R.error("没有查询到该员工信息。");
    }

}
