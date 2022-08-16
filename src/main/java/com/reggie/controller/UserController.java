package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.reggie.common.R;
import com.reggie.pojo.User;
import com.reggie.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Random;

@RequestMapping("/user")
@RestController
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /*发送验证码*/
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        log.info("User信息：{}", user);
//        获取手机号
        String phone = user.getPhone();
//        随机生成四位数的验证信息码

        if (StringUtils.isNotEmpty(phone)) {
            Random random = new Random();
            String code = "";
            for (int i = 0; i < 6; i++) {
                code += random.nextInt(10);
            }
            log.info("code:{}", code);

//        将生成的验证码保存到Session中
            session.setAttribute(phone, code);
            return R.success("手机验证码发送成功！");
        }
        return R.error("手机验证码发送失败!");
    }

    /*移动端用户登录*/
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        log.info("map信息：{}", map);
//        获取手机号
        String phone = map.get("phone").toString();
//        获取验证码
        String code =map.get("code").toString();

//        从session中获取后台生成的code
        Object codeInSession = session.getAttribute(phone);

//        进行检验对比
        if(codeInSession !=null && codeInSession.equals(code)){
            //        如果对比成功就说明可以成功登录
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user=userService.getOne(queryWrapper);
            //        判断是否为新用户，如果是就直接注册成功，在数据库中添加改用户信息
            if(user==null){
                user=new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            return R.success(user);
        }
        return R.error("登录失败!");
    }

    /*移动端用户退出登录*/
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
//        清理Session中保存的当前用户的信息id
        request.getSession().removeAttribute("user");
        return R.success("退出成功！");
    }


}
