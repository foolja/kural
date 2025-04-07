package com.lja.kural.Vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortScanResultVo {
    private String taskId;
    private Map<String, List<SingleTargetPortInfo>> result;
    // PortInfo 类，表示每个端口的详细信息
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SingleTargetPortInfo {
        private int port;
        private String serviceType;
        private String serviceVersion;
    }
}
