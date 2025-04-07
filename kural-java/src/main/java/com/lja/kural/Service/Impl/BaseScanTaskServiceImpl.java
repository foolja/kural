package com.lja.kural.Service.Impl;

import com.lja.kural.Bean.BaseScanTask;
import com.lja.kural.Mapper.BaseScanTaskMapper;
import com.lja.kural.Service.BaseScanTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
public class BaseScanTaskServiceImpl implements BaseScanTaskService {
    @Autowired
    private BaseScanTaskMapper baseScanTaskMapper;
    @Override
    @Transactional
    public List<BaseScanTask> selectBaseScanTaskList(String creatorId) {

        List<BaseScanTask> baseScanTasks = baseScanTaskMapper.selectBaseScanTaskList(creatorId);
        for(int i = 0; i<baseScanTasks.size();i++)
        {
            baseScanTasks.get(i).setId(i+1);
        }
        return baseScanTasks;
    }

    @Override
    public BaseScanTask selectBaseScanTask(String taskId) {
        return baseScanTaskMapper.selectBaseScanTask(taskId);
    }

    @Override
    public boolean checkTaskNameExists(String taskName) {
        return baseScanTaskMapper.checkTaskNameExists(taskName);
    }

    @Override
    public String selectTaskType(String taskId) {
        return baseScanTaskMapper.selectTaskType(taskId);
    }

    @Override
    public void updateTaskProgress(String taskId, Integer progress) {
        baseScanTaskMapper.updateTaskProgress(taskId,progress);
    }

    @Override
    public Integer selectTaskProgress(String taskId) {
        return baseScanTaskMapper.selectTaskProgress(taskId);
    }

    @Override
    public void updateTaskStatus(String taskId, String status) {
        baseScanTaskMapper.updateTaskStatus(taskId,status);
    }
}
