package com.lja.kural.Controller;

import com.lja.kural.Bean.User;
import com.lja.kural.Service.UserService;
import com.lja.kural.Vo.Result;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
@SuppressWarnings({"all"})
@RestController
@RequestMapping("/api/v1")
public class LoginController {
    @Autowired
    private UserService userService;
    @PostMapping("/login")
    public Result doLogin(@RequestBody User user)
    {
        UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(), user.getPassword());
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(token);// 登录成功后，Shiro 会自动为用户创建会话
            String userId = userService.selectUserId(user);
            Session session = subject.getSession();
            session.setAttribute("username",user.getUsername());
            session.setAttribute("userId",userId);
            return Result.success("登录成功",userId);
        } catch (Exception e) {
            return Result.error("401","用户名或密码错误");
        }
    }
    @PostMapping("/logout")
    public Result doLogout()
    {
        Subject currentUser = SecurityUtils.getSubject();
        // 判断用户是否已登录
        if (currentUser.isAuthenticated()) {
            currentUser.logout();  // 执行 Shiro 的登出操作，清除当前会话
            return Result.success("登出成功");
        } else {
            return Result.error("400","用户未登录，不能登出");
        }


    }
    @GetMapping("/check-login")
    public Result checkLogin() {
        Subject currentUser = SecurityUtils.getSubject();
        if (currentUser.isAuthenticated()) {
            return Result.success("用户已登录，Session ID: " + currentUser.getSession().getId());
        } else {
            return Result.error("400","用户未登录");
        }
    }
    @RequestMapping("/login-tip")
    public Result loginTip() {
        return Result.error("400","请先登录");
    }

}
