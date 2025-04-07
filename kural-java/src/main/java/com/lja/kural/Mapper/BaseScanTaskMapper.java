package com.lja.kural.Mapper;

import com.lja.kural.Bean.BaseScanTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.data.relational.core.sql.In;

import java.util.List;

@Mapper
public interface BaseScanTaskMapper
{
    public void addBaseScanTask(BaseScanTask baseScanTask);
    public List<BaseScanTask> selectBaseScanTaskList(@Param("creatorId")String creatorId);
    public BaseScanTask selectBaseScanTask(@Param("taskId")String taskId);
    // 查询是否存在特定的 task_name
    @Select("SELECT EXISTS (SELECT 1 FROM base_scan_task WHERE task_name = #{taskName})")
    public boolean checkTaskNameExists(@Param("taskName") String taskName);

    @Select("SELECT task_type FROM base_scan_task WHERE task_id = #{taskId}")
    public String selectTaskType(@Param("taskId") String taskId);
    @Update("UPDATE base_scan_task SET progress = #{progress} WHERE task_id = #{taskId}")
    public void updateTaskProgress(@Param("taskId") String taskId,
                                   @Param("progress") Integer progress);
    @Select("SELECT progress FROM base_scan_task WHERE task_id = #{taskId}")
    public Integer selectTaskProgress(@Param("taskId") String taskId);
    @Update("UPDATE base_scan_task SET status = #{status} WHERE task_id = #{taskId}")
    public void updateTaskStatus(@Param("taskId") String taskId,
                                 @Param("status") String status);

}
