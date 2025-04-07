package com.lja.kural.Bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseScanTask {
    private Integer Id;             // 数据库自增ID
    private String taskId;          // 任务ID
    private String taskName;        // 任务名称
    private String taskType;        // 任务类型，如 “端口扫描”、“Web漏洞扫描”、“弱口令扫描”
    private String target;          // 扫描目标，可以是IP地址、域名等
    private String status;          // 任务状态，如 “待执行”、“正在执行”、“已完成”、“已取消”、“失败”
    private Date createTime;        // 任务创建时间
    private Date updateTime;        // 任务最后更新时间
    private String creatorId;       // 创建者ID
    private String creator;         // 创建者
    private String description;     // 任务描述
    private Integer priority;       // 任务优先级
    private Integer progress;        // 任务进度
    // Getters and Setters
}
