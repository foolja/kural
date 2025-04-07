package com.lja.kural.Mapper;

import com.lja.kural.Bean.PortScanResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PortScanResultMapper {
    public void addPortScanResult(List<PortScanResult> results);
    public List<PortScanResult> selectPortScanResult(@Param("taskId")String taskId);
}
