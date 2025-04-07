package com.lja.kural.Service.Impl;

import com.lja.kural.Bean.PortScanResult;
import com.lja.kural.Mapper.PortScanResultMapper;
import com.lja.kural.Service.PortScanResultService;
import com.lja.kural.Utils.Transformer;
import com.lja.kural.Vo.PortScanResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Slf4j
@Service
public class PortScanResultServiceImpl implements PortScanResultService {

    @Autowired
    private PortScanResultMapper portScanResultMapper;
    @Override
    @Transactional
    public void addPortScanResult(PortScanResultVo portScanResultVo) {
        List<PortScanResult> portScanResults = Transformer.convertToPortScanResultList(portScanResultVo);
        log.info("添加扫描结果:{}",portScanResults);
        if(!portScanResults.isEmpty())  {
            portScanResultMapper.addPortScanResult(portScanResults);
        }
    }
    @Override
    @Transactional
    public PortScanResultVo selectPortScanResult(String taskId)
    {
        List<PortScanResult> portScanResults = portScanResultMapper.selectPortScanResult(taskId);
        return Transformer.convertToPortScanResultVo(portScanResults);
    }
}
