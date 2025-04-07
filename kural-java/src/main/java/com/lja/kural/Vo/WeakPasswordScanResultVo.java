package com.lja.kural.Vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeakPasswordScanResultVo {
    private String taskId;
    private Map<String, List<SingleTargetWeakPasswordScanInfo>> result;
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SingleTargetWeakPasswordScanInfo{
        private int port;
        private String service;
        private String username;
        private String password;
    }
}
