package com.lja.kural.Service;

import com.lja.kural.Bean.WeakPasswordScanTask;
import com.lja.kural.Vo.Result;


public interface WeakPasswordScanTaskService {
    public String addWeakPasswordScanTask(WeakPasswordScanTask weakPasswordScanTask);
    public WeakPasswordScanTask selectWeakPasswordScanTask(String taskId);

    public Result startWeakPasswordScanTask(String taskId);
}
