package com.lja.kural.Service;

import com.lja.kural.Bean.WeakPasswordScanTask;
import com.lja.kural.Bean.WebVulnScanTask;
import com.lja.kural.Vo.Result;

public interface WebVulnScanTaskService {
    public String addWebVulnScanTask(WebVulnScanTask webVulnScanTask);

    public WebVulnScanTask selectWebVulnScanTask(String taskId);

    public Result startWebVulnScanTask(String taskId);
}
