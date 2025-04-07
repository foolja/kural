package com.lja.kural.Bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeakPasswordScanResult {
    private Integer id;             // 数据库自增ID
    private String taskId;          // 任务Id
    private String target;          // 目标
    private String port;            // 爆破服务类型对应的开放端口号
    private String service;         // 爆破服务类型（如 SSH、FTP）
    private String username;        // 爆破成功的用户名
    private String password;        // 爆破成功的密码
    private Date scanTime;          // 扫描时间
    private String additionalInfo;  // 额外的扫描信息

}
