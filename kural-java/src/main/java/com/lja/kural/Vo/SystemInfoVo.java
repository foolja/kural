package com.lja.kural.Vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class SystemInfoVo {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CpuInfo{
        Integer cpuLogicalProcessorCount;
        Integer cpuPhysicalProcessorCount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemoryInfo{
        String totalSize;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiskInfo{
        Map<String, Double> totalSize;
        Map<String, Double> usableSize;

    }
    private CpuInfo cpuInfo;
    private MemoryInfo memoryInfo;
    private DiskInfo diskInfo;



}
