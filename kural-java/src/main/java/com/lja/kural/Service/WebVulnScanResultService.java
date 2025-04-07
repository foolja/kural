package com.lja.kural.Service;

import com.lja.kural.Vo.WebVulnScanResultVo;

public interface WebVulnScanResultService {
    public void addWebVulnScanResult(WebVulnScanResultVo webVulnScanResultVo);
    public WebVulnScanResultVo selectWebVulnScanResult(String taskId);
}
