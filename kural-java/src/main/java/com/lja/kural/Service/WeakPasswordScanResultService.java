package com.lja.kural.Service;

import com.lja.kural.Vo.WeakPasswordScanResultVo;

public interface WeakPasswordScanResultService {
    public void addWeakPasswordScanResult(WeakPasswordScanResultVo weakPasswordScanResultVo);
    public WeakPasswordScanResultVo selectWeakPasswordScanResult(String taskId);
}
