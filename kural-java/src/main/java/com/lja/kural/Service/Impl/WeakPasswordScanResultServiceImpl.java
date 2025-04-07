package com.lja.kural.Service.Impl;

import com.lja.kural.Bean.WeakPasswordScanResult;
import com.lja.kural.Mapper.WeakPasswordScanResultMapper;
import com.lja.kural.Service.WeakPasswordScanResultService;
import com.lja.kural.Utils.Transformer;
import com.lja.kural.Vo.WeakPasswordScanResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WeakPasswordScanResultServiceImpl implements WeakPasswordScanResultService {
    @Autowired
    private WeakPasswordScanResultMapper weakPasswordScanResultMapper;
    @Override
    @Transactional
    public void addWeakPasswordScanResult(WeakPasswordScanResultVo weakPasswordScanResultVo) {
        List<WeakPasswordScanResult> weakPasswordScanResultList = Transformer.convertToWeakPasswordScanResultList(weakPasswordScanResultVo);
        if(!weakPasswordScanResultList.isEmpty()){
            weakPasswordScanResultMapper.addWeakPasswordScanResult(weakPasswordScanResultList);
        }
    }

    @Override
    @Transactional
    public WeakPasswordScanResultVo selectWeakPasswordScanResult(String taskId) {
        List<WeakPasswordScanResult> weakPasswordScanResultList = weakPasswordScanResultMapper.selectWeakPasswordScanResult(taskId);
        return Transformer.convertToWeakPasswordScanResultVo(weakPasswordScanResultList);
    }
}
