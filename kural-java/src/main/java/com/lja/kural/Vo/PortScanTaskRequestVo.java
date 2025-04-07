package com.lja.kural.Vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortScanTaskRequestVo {
    private String taskId;      // 任务唯一标识（必须）
    private String taskType;    // 任务类型（必须）
    private String target;      // 目标地址（必须）
    private Integer priority;   // 优先级（可选）

    // PortScanTask 特有字段
    private String portList;
    private Boolean isFullScan;
    private String scanMethod;
}
