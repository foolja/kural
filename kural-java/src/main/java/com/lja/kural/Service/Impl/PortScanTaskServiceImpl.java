package com.lja.kural.Service.Impl;

import com.lja.kural.Bean.BaseScanTask;
import com.lja.kural.Bean.PortScanTask;
import com.lja.kural.Component.GinTaskClient;

import com.lja.kural.Mapper.BaseScanTaskMapper;
import com.lja.kural.Mapper.PortScanTaskMapper;
import com.lja.kural.Service.PortScanTaskService;
import com.lja.kural.Utils.IdUtil;
import com.lja.kural.Vo.PortScanTaskRequestVo;
import com.lja.kural.Vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class PortScanTaskServiceImpl implements PortScanTaskService
{
    @Autowired
    private BaseScanTaskMapper baseScanTaskMapper;
    @Autowired
    private PortScanTaskMapper portScanTaskMapper;
    @Autowired
    private GinTaskClient ginTaskClient;

    @Override
    @Transactional
    public String addPortScanTask(PortScanTask portScanTask)
    {
        portScanTask.setTaskId(IdUtil.generateTaskId());
        // 插入基础扫描任务
        baseScanTaskMapper.addBaseScanTask((BaseScanTask)portScanTask);
        // 插入端口扫描任务
        portScanTaskMapper.addPortScanTask(portScanTask);
        return portScanTask.getTaskId();
    }

    @Override
    @Transactional
    public PortScanTask selectPortScanTask(String taskId) {
        PortScanTask portScanTask = portScanTaskMapper.selectPortScanTask(taskId);
        return portScanTask;
    }

    @Override
    @Transactional
    public Result startPortScanTask(String taskId) {
        try {
            PortScanTask portScanTask = portScanTaskMapper.selectPortScanTask(taskId);
            PortScanTaskRequestVo portScanTaskRequestVo = new PortScanTaskRequestVo();
            portScanTaskRequestVo.setIsFullScan(portScanTask.getIsFullScan());
            portScanTaskRequestVo.setPortList(portScanTask.getPortList());
            portScanTaskRequestVo.setScanMethod(portScanTask.getScanMethod());
            portScanTaskRequestVo.setTaskType(portScanTask.getTaskType());
            portScanTaskRequestVo.setTaskId(portScanTask.getTaskId());
            portScanTaskRequestVo.setPriority(portScanTask.getPriority());
            portScanTaskRequestVo.setTarget(portScanTask.getTarget());
            ginTaskClient.startTaskAndListenProgress(taskId,portScanTaskRequestVo);
            baseScanTaskMapper.updateTaskStatus(taskId,"运行中");
            return Result.success("任务开启成功");
        } catch (Exception e) {
            return Result.error("500","错误");
        }


    }

}
