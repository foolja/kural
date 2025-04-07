package com.lja.kural.Mapper;


import com.lja.kural.Bean.WeakPasswordScanResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface WeakPasswordScanResultMapper {
    public void addWeakPasswordScanResult(List<WeakPasswordScanResult> results);
    public List<WeakPasswordScanResult> selectWeakPasswordScanResult(@Param("taskId")String taskId);
}
