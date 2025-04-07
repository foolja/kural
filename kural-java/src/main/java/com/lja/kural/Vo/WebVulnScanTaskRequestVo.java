package com.lja.kural.Vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebVulnScanTaskRequestVo {
    private String taskId;      // 任务唯一标识
    private String taskType;    // 任务类型
    private String target;      // 目标地址
    private Integer priority;   // 优先级
    // WebVulnScanTask 特有字段
    private String pocContent;
}
