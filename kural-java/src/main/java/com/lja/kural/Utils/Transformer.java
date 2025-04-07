package com.lja.kural.Utils;

import com.lja.kural.Bean.PortScanResult;
import com.lja.kural.Bean.WeakPasswordScanResult;
import com.lja.kural.Bean.WebVulnScanResult;
import com.lja.kural.Vo.PortScanResultVo;
import com.lja.kural.Vo.WeakPasswordScanResultVo;
import com.lja.kural.Vo.WebVulnScanResultVo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Transformer {
    public static List<PortScanResult> convertToPortScanResultList(PortScanResultVo portScanResultVo) {
        List<PortScanResult> portScanResultList = new ArrayList<>();
        for (Map.Entry<String, List<PortScanResultVo.SingleTargetPortInfo>> entry : portScanResultVo.getResult().entrySet()) {
            String taskId = portScanResultVo.getTaskId(); // 任务ID
            String target = entry.getKey();
            // 获取当前 IP 对应的端口信息列表
            List<PortScanResultVo.SingleTargetPortInfo> portInfoList = entry.getValue();

            // 遍历每个端口信息，将其转换为 PortScanResult 对象
            for (PortScanResultVo.SingleTargetPortInfo portInfo : portInfoList) {
                // 创建 PortScanResult 对象
                PortScanResult portScanResult = new PortScanResult(
                        0,
                        taskId, // 任务ID
                        target,
                        String.valueOf(portInfo.getPort()), // 开放端口号
                        portInfo.getServiceType(), // 服务名称
                        portInfo.getServiceVersion(), // 服务版本
                        null, // 扫描时间
                        null // 额外信息
                );

                // 添加到结果列表
                portScanResultList.add(portScanResult);
            }
        }

        return portScanResultList;
    }
    public static PortScanResultVo convertToPortScanResultVo(List<PortScanResult> portScanResultList) {
        if (portScanResultList == null || portScanResultList.isEmpty()) {
            return new PortScanResultVo(null, Collections.emptyMap());
        }

        // 提取公共字段（假设所有条目的 taskId 相同）
        String taskId = portScanResultList.get(0).getTaskId();

        // 按 target 分组，并转换为 PortInfo 列表
        Map<String, List<PortScanResultVo.SingleTargetPortInfo>> resultMap = portScanResultList.stream()
                .collect(Collectors.groupingBy(
                        PortScanResult::getTarget,
                        Collectors.mapping(
                                result -> new PortScanResultVo.SingleTargetPortInfo(
                                        Integer.parseInt(result.getOpenPort()), // 端口号转 int
                                        result.getService(),                    // 服务类型
                                        result.getVersion()                     // 服务版本
                                ),
                                Collectors.toList()
                        )
                ));

        return new PortScanResultVo(taskId, resultMap);
    }
    public static List<WeakPasswordScanResult> convertToWeakPasswordScanResultList(WeakPasswordScanResultVo weakPasswordScanResultVo){
        List<WeakPasswordScanResult> weakPasswordScanResultList = new ArrayList<>();
        for (Map.Entry<String, List<WeakPasswordScanResultVo.SingleTargetWeakPasswordScanInfo>> entry : weakPasswordScanResultVo.getResult().entrySet()) {
            String taskId = weakPasswordScanResultVo.getTaskId(); // 任务ID
            String target = entry.getKey();
            // 获取当前 IP 对应的端口信息列表
            List<WeakPasswordScanResultVo.SingleTargetWeakPasswordScanInfo> weakPasswordScanInfoList = entry.getValue();


            for (WeakPasswordScanResultVo.SingleTargetWeakPasswordScanInfo targetInfo : weakPasswordScanInfoList) {
                // 创建 PortScanResult 对象
                WeakPasswordScanResult weakPasswordScanResult = new WeakPasswordScanResult(
                        0,
                        taskId,
                        target,
                        String.valueOf(targetInfo.getPort()),
                        targetInfo.getService(),
                        targetInfo.getUsername(),
                        targetInfo.getPassword(),
                        null,
                        null
                );

                // 添加到结果列表
                weakPasswordScanResultList.add(weakPasswordScanResult);
            }
        }

        return weakPasswordScanResultList;
    }
    public static WeakPasswordScanResultVo convertToWeakPasswordScanResultVo(List<WeakPasswordScanResult> weakPasswordScanResultList){
        if (weakPasswordScanResultList == null || weakPasswordScanResultList.isEmpty()) {
            return new WeakPasswordScanResultVo(null, Collections.emptyMap());
        }
        // 提取公共字段（假设所有条目的 taskId 相同）
        String taskId = weakPasswordScanResultList.get(0).getTaskId();

        // 按 target 分组，并转换为 PortInfo 列表
        Map<String, List<WeakPasswordScanResultVo.SingleTargetWeakPasswordScanInfo>> resultMap = weakPasswordScanResultList.stream()
                .collect(Collectors.groupingBy(
                        WeakPasswordScanResult::getTarget,
                        Collectors.mapping(
                                result -> new WeakPasswordScanResultVo.SingleTargetWeakPasswordScanInfo(
                                        Integer.parseInt(result.getPort()), // 端口号转 int
                                        result.getService(),                // 服务类型
                                        result.getUsername(),               // 用户名
                                        result.getPassword()                // 密码
                                ),
                                Collectors.toList()
                        )
                ));

        return new WeakPasswordScanResultVo(taskId, resultMap);
    }

    public static List<WebVulnScanResult> convertToWebVulnScanResultList(WebVulnScanResultVo webVulnScanResultVo){
        List<WebVulnScanResult> webVulnScanResultList = new ArrayList<>();
        for (Map.Entry<String, List<WebVulnScanResultVo.SingleTargetWebVulnScanInfo>> entry : webVulnScanResultVo.getResult().entrySet()){
            String taskId = webVulnScanResultVo.getTaskId(); // 任务ID
            String target = entry.getKey();
            // 获取当前 IP 对应的端口信息列表
            List<WebVulnScanResultVo.SingleTargetWebVulnScanInfo> webVulnScanInfoList = entry.getValue();
            for (WebVulnScanResultVo.SingleTargetWebVulnScanInfo targetInfo : webVulnScanInfoList){
                WebVulnScanResult webVulnScanResult = new WebVulnScanResult(
                        0,
                        taskId,
                        target,
                        targetInfo.getPocFilePath(),
                        null,
                        null
                );

                // 添加到结果列表
                webVulnScanResultList.add(webVulnScanResult);
            }
        }
        return webVulnScanResultList;
    }
    public static WebVulnScanResultVo convertToWebVulnScanResultVo(List<WebVulnScanResult> webVulnScanResultList){
        if (webVulnScanResultList == null || webVulnScanResultList.isEmpty()) {
            return new WebVulnScanResultVo(null, Collections.emptyMap());
        }
        // 提取公共字段（假设所有条目的 taskId 相同）
        String taskId = webVulnScanResultList.get(0).getTaskId();
        // 按 target 分组，并转换为 PortInfo 列表
        Map<String, List<WebVulnScanResultVo.SingleTargetWebVulnScanInfo>> resultMap = webVulnScanResultList.stream()
                .collect(Collectors.groupingBy(
                        WebVulnScanResult::getTarget,
                        Collectors.mapping(
                                result -> new WebVulnScanResultVo.SingleTargetWebVulnScanInfo(
                                       result.getPocFilePath()
                                ),
                                Collectors.toList()
                        )
                ));
        return new WebVulnScanResultVo(taskId, resultMap);
    }

}
