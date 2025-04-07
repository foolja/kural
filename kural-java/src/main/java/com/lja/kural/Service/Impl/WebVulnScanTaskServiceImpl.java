package com.lja.kural.Service.Impl;

import com.lja.kural.Bean.BaseScanTask;
import com.lja.kural.Bean.WebVulnScanTask;
import com.lja.kural.Component.GinTaskClient;
import com.lja.kural.Mapper.BaseScanTaskMapper;
import com.lja.kural.Mapper.WebVulnScanTaskMapper;
import com.lja.kural.Service.WebVulnScanTaskService;
import com.lja.kural.Utils.IdUtil;
import com.lja.kural.Vo.Result;
import com.lja.kural.Vo.WebVulnScanTaskRequestVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Slf4j
@Service
public class WebVulnScanTaskServiceImpl implements WebVulnScanTaskService {
    @Autowired
    private BaseScanTaskMapper baseScanTaskMapper;
    @Autowired
    private WebVulnScanTaskMapper webVulnScanTaskMapper;
    @Autowired
    private GinTaskClient ginTaskClient;
    @Override
    @Transactional
    public String addWebVulnScanTask(WebVulnScanTask webVulnScanTask) {
        webVulnScanTask.setTaskId(IdUtil.generateTaskId());
        baseScanTaskMapper.addBaseScanTask((BaseScanTask) webVulnScanTask);
        webVulnScanTaskMapper.addWebVulnScanTask(webVulnScanTask);
        return webVulnScanTask.getTaskId();
    }

    @Override
    public WebVulnScanTask selectWebVulnScanTask(String taskId) {
        return webVulnScanTaskMapper.selectWebVulnScanTask(taskId);
    }

    @Override
    public Result startWebVulnScanTask(String taskId) {
        try {
            WebVulnScanTask webVulnScanTask = webVulnScanTaskMapper.selectWebVulnScanTask(taskId);
            WebVulnScanTaskRequestVo webVulnScanTaskRequestVo = new WebVulnScanTaskRequestVo();
            webVulnScanTaskRequestVo.setTaskId(webVulnScanTask.getTaskId());
            webVulnScanTaskRequestVo.setTaskType(webVulnScanTask.getTaskType());
            webVulnScanTaskRequestVo.setTarget(webVulnScanTask.getTarget());
            webVulnScanTaskRequestVo.setPriority(webVulnScanTask.getPriority());
            webVulnScanTaskRequestVo.setPocContent(webVulnScanTask.getPocContent());
            ginTaskClient.startTaskAndListenProgress(taskId,webVulnScanTaskRequestVo);
            baseScanTaskMapper.updateTaskStatus(taskId,"运行中");
            return Result.success("任务开启成功");
        } catch (Exception e) {
            log.error("任务{}开启失败",taskId);
            return Result.error("400","任务开启失败");
        }
    }


}
