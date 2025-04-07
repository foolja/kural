package com.lja.kural.Service.Impl;

import com.lja.kural.Bean.WebVulnScanResult;
import com.lja.kural.Mapper.WebVulnScanResultMapper;
import com.lja.kural.Service.WebVulnScanResultService;
import com.lja.kural.Utils.Transformer;
import com.lja.kural.Vo.WebVulnScanResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
public class WebVulnScanResultServiceImpl implements WebVulnScanResultService {
    @Autowired
    private WebVulnScanResultMapper webVulnScanResultMapper;
    @Override
    @Transactional
    public void addWebVulnScanResult(WebVulnScanResultVo webVulnScanResultVo) {
        List<WebVulnScanResult> webVulnScanResultList = Transformer.convertToWebVulnScanResultList(webVulnScanResultVo);
        if(!webVulnScanResultList.isEmpty()){
            webVulnScanResultMapper.addWebVulnScanResult(webVulnScanResultList);
        }


    }

    @Override
    public WebVulnScanResultVo selectWebVulnScanResult(String taskId) {
        List<WebVulnScanResult> webVulnScanResultList = webVulnScanResultMapper.selectWebVulnScanResult(taskId);
        return Transformer.convertToWebVulnScanResultVo(webVulnScanResultList);
    }
}
