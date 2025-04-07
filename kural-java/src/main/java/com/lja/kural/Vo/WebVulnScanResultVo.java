package com.lja.kural.Vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebVulnScanResultVo {
    private String taskId;
    private Map<String, List<SingleTargetWebVulnScanInfo>> result;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SingleTargetWebVulnScanInfo{
        private String pocFilePath;
    }
}
