package com.lja.kural.Service;

import com.lja.kural.Bean.PortScanTask;
import com.lja.kural.Vo.Result;

public interface PortScanTaskService
{
    /**
     * 本方法是创建端口存活探测任务
     * @param portScanTask：端口存活探测任务
     * @return 返回任务ID
     */
    public String addPortScanTask(PortScanTask portScanTask);
    public PortScanTask selectPortScanTask(String taskId);
    public Result startPortScanTask(String taskId);
}
