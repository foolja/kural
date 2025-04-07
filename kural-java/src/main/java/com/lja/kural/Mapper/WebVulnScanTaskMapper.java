package com.lja.kural.Mapper;

import com.lja.kural.Bean.WebVulnScanTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface WebVulnScanTaskMapper {
    public void addWebVulnScanTask(WebVulnScanTask webVulnScanTask);
    public WebVulnScanTask selectWebVulnScanTask(@Param("taskId")String taskId);
}
