package com.lja.kural.Service.Impl;

import com.lja.kural.Bean.BaseScanTask;
import com.lja.kural.Bean.WeakPasswordScanTask;
import com.lja.kural.Component.GinTaskClient;
import com.lja.kural.Mapper.BaseScanTaskMapper;
import com.lja.kural.Mapper.WeakPasswordScanTaskMapper;
import com.lja.kural.Service.WeakPasswordScanTaskService;
import com.lja.kural.Utils.IdUtil;
import com.lja.kural.Vo.Result;
import com.lja.kural.Vo.WeakPasswordScanTaskRequestVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class WeakPasswordScanTaskServiceImpl implements WeakPasswordScanTaskService {
    @Autowired
    private BaseScanTaskMapper baseScanTaskMapper;
    @Autowired
    private WeakPasswordScanTaskMapper weakPasswordScanTaskMapper;

    @Autowired
    private GinTaskClient ginTaskClient;

    @Override
    @Transactional
    public String addWeakPasswordScanTask(WeakPasswordScanTask weakPasswordScanTask) {
        weakPasswordScanTask.setServiceTypes(String.join(",", weakPasswordScanTask.getServiceTypeList()));
        weakPasswordScanTask.setTaskId(IdUtil.generateTaskId());
        baseScanTaskMapper.addBaseScanTask((BaseScanTask) weakPasswordScanTask);
        weakPasswordScanTaskMapper.addWeakPasswordScanTask(weakPasswordScanTask);
        return weakPasswordScanTask.getTaskId();

    }

    @Override
    @Transactional
    public WeakPasswordScanTask selectWeakPasswordScanTask(String taskId) {
        return weakPasswordScanTaskMapper.selectWeakPasswordScanTask(taskId);
    }

    @Override
    public Result startWeakPasswordScanTask(String taskId) {
        try {
            WeakPasswordScanTask weakPasswordScanTask = weakPasswordScanTaskMapper.selectWeakPasswordScanTask(taskId);
            WeakPasswordScanTaskRequestVo weakPasswordScanTaskRequestVo = new WeakPasswordScanTaskRequestVo();
            weakPasswordScanTaskRequestVo.setTaskType(weakPasswordScanTask.getTaskType());
            weakPasswordScanTaskRequestVo.setPasswordFilePath(weakPasswordScanTask.getPasswordFilePath());
            weakPasswordScanTaskRequestVo.setUsernameFilePath(weakPasswordScanTask.getUsernameFilePath());
            weakPasswordScanTaskRequestVo.setUseDefaultDict(weakPasswordScanTask.getUseDefaultDict());
            weakPasswordScanTaskRequestVo.setServiceTypes(weakPasswordScanTask.getServiceTypes());
            weakPasswordScanTaskRequestVo.setTaskId(weakPasswordScanTask.getTaskId());
            weakPasswordScanTaskRequestVo.setPriority(weakPasswordScanTask.getPriority());
            weakPasswordScanTaskRequestVo.setTarget(weakPasswordScanTask.getTarget());
            ginTaskClient.startTaskAndListenProgress(taskId,weakPasswordScanTaskRequestVo);
            baseScanTaskMapper.updateTaskStatus(taskId,"运行中");
            return Result.success("任务开启成功");
        } catch (Exception e) {
            log.error("任务{}开启失败",taskId);
            return Result.error("400","任务开启失败");
        }


    }
}
