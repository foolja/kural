package com.lja.kural.Bean;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=true)
public class PortScanTask extends BaseScanTask{
    private String portList;        // 需要扫描的端口列表
    private Boolean isFullScan;     // 是否进行全端口扫描
    private String scanMethod;      // 扫描方法，如SYN扫描(半连接扫描),TCP连接(全连接扫描),UDP扫描等
    // Getters and Setters
}
