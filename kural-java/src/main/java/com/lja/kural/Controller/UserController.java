package com.lja.kural.Controller;

import com.lja.kural.Bean.User;
import com.lja.kural.Service.UserService;
import com.lja.kural.Vo.Result;
import com.lja.kural.Vo.UserVo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
@SuppressWarnings({"all"})
@RestController
@RequestMapping("/api/v1")
public class UserController {
    @Autowired
    private UserService userService;
    @PostMapping("/user")
    public Result addUser(@RequestBody User user)
    {
        String userId = userService.addUser(user);
        return Result.success("添加用户成功",userId);
    }
    @GetMapping("/userInfo")
    public Result selectUserName()
    {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            Session session = subject.getSession();
            return Result.success(
                    new UserVo((String) session.getAttribute("userId"),(String) session.getAttribute("username"))
            );
        }else{
            return Result.error("400","未登录");
        }
    }


}
