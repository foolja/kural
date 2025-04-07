package com.lja.kural.Bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebVulnScanResult {
    private Integer id;             // 数据库自增ID
    private String taskId;          // 任务Id
    private String target;          // 目标url
    private String pocFilePath;     // 扫描到的漏洞对应的poc文件路径
    private Date scanTime;          // 扫描时间
    private String additionalInfo;  // 单个额外信息
}
