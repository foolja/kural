package com.lja.kural.Service;

import com.lja.kural.Vo.PortScanResultVo;

public interface PortScanResultService {
  public void addPortScanResult(PortScanResultVo portScanResultVo);
  public PortScanResultVo selectPortScanResult(String taskId);
}
