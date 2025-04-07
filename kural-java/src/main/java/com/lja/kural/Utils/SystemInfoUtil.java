package com.lja.kural.Utils;

import com.lja.kural.Vo.SystemInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@SuppressWarnings({"all"})
public class SystemInfoUtil {
    private static final SystemInfo systemInfo = new SystemInfo();
    private static final OperatingSystem operatingSystem = systemInfo.getOperatingSystem();
    private static final HardwareAbstractionLayer hal = systemInfo.getHardware();
    private static final CentralProcessor processor = hal.getProcessor();

    private static final GlobalMemory globalMemory = hal.getMemory();
    /**
     * 获取CPU使用率
     * @return
     */
    public static Double getCpuUsageRate(){
        return processor.getSystemCpuLoad(1000) * 100;
    }

    /**
     * 获取内存使用率
     * @return
     */
    public static Double getMemoryUsageRate(){
        return 100d * (globalMemory.getTotal() - globalMemory.getAvailable()) / globalMemory.getTotal();
    }

    /**
     * 获取可用内存大小
     * @return
     */
    public static String getMemoryAvailable(){
        return FormatUtil.formatBytes(globalMemory.getAvailable());
    }

    /**
     * 获取磁盘使用率
     * @return
     */
    public static Map<String,Double> getDiskUsageRate(){
        ConcurrentHashMap<String, Double> resultMap = new ConcurrentHashMap<>();
        for (OSFileStore fs : operatingSystem.getFileSystem().getFileStores()) {
            long totalSpace = fs.getTotalSpace();
            long usableSpace = fs.getUsableSpace();
            double usage = ((double) (totalSpace - usableSpace) / totalSpace) * 100;
            resultMap.put(fs.getMount(),usage);
        }
        return resultMap;
    }





    /**
     * 获取CPU逻辑核心数和物理核心数
     * @return
     */
    public static SystemInfoVo.CpuInfo getCpuInfo() {
        SystemInfoVo.CpuInfo cpuInfo = new SystemInfoVo.CpuInfo();
        cpuInfo.setCpuLogicalProcessorCount(processor.getLogicalProcessorCount());
        cpuInfo.setCpuPhysicalProcessorCount(processor.getPhysicalProcessorCount());
        return cpuInfo;
    }

    /**
     * 获取内存总大小
     * @return
     */
    public static SystemInfoVo.MemoryInfo getMemoryInfo(){
        SystemInfoVo.MemoryInfo memoryInfo = new SystemInfoVo.MemoryInfo();
        memoryInfo.setTotalSize(FormatUtil.formatBytes(globalMemory.getTotal()));
        return memoryInfo;
    }

    /**
     * 获取磁盘总大小
     * @return
     */
    public static SystemInfoVo.DiskInfo getDiskInfo(){
        SystemInfoVo.DiskInfo diskInfo = new SystemInfoVo.DiskInfo();
        ConcurrentHashMap<String, Double> totalSizeMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, Double> usedSizeMap = new ConcurrentHashMap<>();
        for (OSFileStore fs : operatingSystem.getFileSystem().getFileStores()) {
            long totalSpace = fs.getTotalSpace();
            long usableSpace = fs.getUsableSpace();
            double totalGB = (double) totalSpace / (1024 * 1024 * 1024);
            double usableGB = (double) usableSpace / (1024 * 1024 * 1024);
            totalSizeMap.put(fs.getMount(),totalGB);
            usedSizeMap.put(fs.getMount(),usableGB);
        }
        diskInfo.setTotalSize(totalSizeMap);
        diskInfo.setUsableSize(usedSizeMap);
        return diskInfo;
    }

}
