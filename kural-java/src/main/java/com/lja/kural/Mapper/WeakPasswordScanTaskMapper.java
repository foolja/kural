package com.lja.kural.Mapper;


import com.lja.kural.Bean.WeakPasswordScanTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface WeakPasswordScanTaskMapper {
    public void addWeakPasswordScanTask(WeakPasswordScanTask weakPasswordScanTask);

    public WeakPasswordScanTask selectWeakPasswordScanTask(@Param("taskId")String taskId);

}
