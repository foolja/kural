package com.lja.kural.Controller;

import com.lja.kural.Utils.SystemInfoUtil;
import com.lja.kural.Vo.Result;
import com.lja.kural.Vo.SystemInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import oshi.SystemInfo;

@SuppressWarnings({"all"})
@RestController
@RequestMapping("/api/v1")
public class SystemController {

    @GetMapping("/system/hardware/Info")
    public Result getCpuInfo()
    {
        SystemInfoVo systemInfoVo = new SystemInfoVo();
        systemInfoVo.setCpuInfo(SystemInfoUtil.getCpuInfo());
        systemInfoVo.setMemoryInfo(SystemInfoUtil.getMemoryInfo());
        systemInfoVo.setDiskInfo(SystemInfoUtil.getDiskInfo());
        return Result.success("查询成功",systemInfoVo);
    }
}
