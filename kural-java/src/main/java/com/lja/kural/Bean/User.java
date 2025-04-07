package com.lja.kural.Bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Integer Id;                // 数据库自增ID
    private String userId;             // 用户ID
    private String username;        // 用户名
    private String password;        // 用户密码
    private Date createTime;        // 账户创建时间
}
