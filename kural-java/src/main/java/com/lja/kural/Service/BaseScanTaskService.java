package com.lja.kural.Service;

import com.lja.kural.Bean.BaseScanTask;


import java.util.List;

public interface BaseScanTaskService {
    public List<BaseScanTask> selectBaseScanTaskList(String creatorId);
    public BaseScanTask selectBaseScanTask(String taskId);
    public boolean checkTaskNameExists(String taskName);
    public String selectTaskType(String taskId);

    public void updateTaskProgress(String taskId, Integer progress);

    public Integer selectTaskProgress(String taskId);

    public void updateTaskStatus(String taskId, String status);
}
