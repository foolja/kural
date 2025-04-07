package com.lja.kural.Mapper;

import com.lja.kural.Bean.PortScanTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PortScanTaskMapper
{
    public void addPortScanTask(PortScanTask portScanTask);
    public PortScanTask selectPortScanTask(@Param("taskId")String taskId);
}
