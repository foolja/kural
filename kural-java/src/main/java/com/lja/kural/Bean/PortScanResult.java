package com.lja.kural.Bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortScanResult {
    private Integer Id;             // 数据库自增id
    private String taskId;          // 任务ID
    private String target;          // 扫描目标
    private String openPort;        // 单个开放端口号
    private String service;         // 单个服务名称
    private String version;         // 单个版本
    private Date scanTime;          // 扫描时间
    private String additionalInfo;  // 单个额外信息
}

