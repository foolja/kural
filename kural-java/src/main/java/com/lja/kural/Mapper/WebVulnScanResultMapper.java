package com.lja.kural.Mapper;


import com.lja.kural.Bean.WebVulnScanResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface WebVulnScanResultMapper {
    public void addWebVulnScanResult(List<WebVulnScanResult> results);

    public List<WebVulnScanResult> selectWebVulnScanResult(@Param("taskId")String taskId);
}
