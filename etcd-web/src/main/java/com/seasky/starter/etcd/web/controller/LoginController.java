package com.seasky.starter.etcd.web.controller;

import com.seasky.starter.etcd.web.entity.User;
import com.seasky.starter.etcd.web.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping(value = "/user/login")
    public String login(@RequestParam("username") String userName,
                        @RequestParam("password") String password,
                        Map<String, Object> map, HttpSession session) {

        if (!StringUtils.isEmpty(userName)) {
            User user = loginService.getUserByUserName(userName);
            if (user != null) {
                if (user.getPassword().equals(password)) {
                    session.setAttribute("loginUser", user.getUserName());
                    return "redirect:/main.html";//登陆成功，防止表单重复提交，可以重定向到主页
                }
            }
        }

        //登陆失败
        map.put("msg", "用户名密码错误");
        return "login";

    }

    @GetMapping(value = "/user/loginOut")
    public String loginOut(HttpSession session) {
        session.invalidate();
        return "login";
    }

    @PostMapping(value = "/user/register")
    public String register(User user, HttpServletRequest request, HttpServletResponse response) {
        if (user == null || StringUtils.isEmpty(user.getUserName())
                || StringUtils.isEmpty(user.getPassword())) {
            request.setAttribute("msg","用户名密码不能为空");
            return "register";
        }
        User userByUserName = loginService.getUserByUserName(user.getUserName());
        if(userByUserName != null){
            request.setAttribute("msg","用户名重复");
            return "register";
        }
        loginService.insertUser(user);
        request.getSession().setAttribute("loginUser",user.getUserName());
        return "redirect:/main.html";
    }

}
