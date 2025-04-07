package com.lja.kural.Controller;

import com.lja.kural.Bean.BaseScanTask;
import com.lja.kural.Bean.PortScanTask;
import com.lja.kural.Bean.WeakPasswordScanTask;
import com.lja.kural.Bean.WebVulnScanTask;
import com.lja.kural.Mapper.WebVulnScanResultMapper;
import com.lja.kural.Service.*;
import com.lja.kural.Utils.KuralLogger;
import com.lja.kural.Utils.SessionUtil;
import com.lja.kural.Vo.PortScanResultVo;
import com.lja.kural.Vo.Result;
import com.lja.kural.Vo.WeakPasswordScanResultVo;
import com.lja.kural.Vo.WebVulnScanResultVo;
import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Controller
 * @ResponseBody
 * 这是一个与扫描任务相关的controller
 */
@SuppressWarnings({"all"})
@RestController
@RequestMapping("/api/v1")
public class TaskController {

    @Autowired
    private PortScanTaskService portScanTaskService;
    @Autowired
    private BaseScanTaskService baseScanTaskService;
    @Autowired
    private WeakPasswordScanTaskService weakPasswordScanTaskService;
    @Autowired
    private WebVulnScanTaskService webVulnScanTaskService;



    @Autowired
    private PortScanResultService portScanResultService;
    @Autowired
    private WeakPasswordScanResultService weakPasswordScanResultService;
    @Autowired
    private WebVulnScanResultService webVulnScanResultService;


    /**
     * 开启指定任务的扫描
     * @return
     */

    @GetMapping("/task/{taskId}/start")
    public Result startScanTask(@PathVariable("taskId")String taskId)
    {
        //查询
        BaseScanTask baseScanTask = baseScanTaskService.selectBaseScanTask(taskId);
        if(baseScanTask.getTaskType().equals("端口扫描")){
           return portScanTaskService.startPortScanTask(taskId);
        }else if(baseScanTask.getTaskType().equals("弱口令扫描")){
            return weakPasswordScanTaskService.startWeakPasswordScanTask(taskId);
        }else if(baseScanTask.getTaskType().equals("Web漏洞扫描")){
            return webVulnScanTaskService.startWebVulnScanTask(taskId);
        }
        return Result.success("任务启动成功");

    }

    /**
     * 通过任务Id获取指定任务的细节
     * @return
     */

    @GetMapping("/task/{taskId}/detail")
    public Result selectTaskDetail(@PathVariable("taskId")String taskId)
    {
        String taskType = baseScanTaskService.selectTaskType(taskId);
        if(taskType.equals("端口扫描")) {
            return Result.success("查询成功",portScanTaskService.selectPortScanTask(taskId));
        }else if(taskType.equals("弱口令扫描")) {
            return Result.success("查询成功",weakPasswordScanTaskService.selectWeakPasswordScanTask(taskId));
        }else if(taskType.equals("Web漏洞扫描")){
            return Result.success("查询成功",webVulnScanTaskService.selectWebVulnScanTask(taskId));
        }else {
            return Result.error("400","不支持该任务类型");
        }
    }
    /**
     * 通过任务Id获取指定任务的扫描结果
     * @return
     */

    @GetMapping("/task/{taskId}/result")
    public Result selectTaskResult(@PathVariable("taskId")String taskId)
    {
        String taskType = baseScanTaskService.selectTaskType(taskId);
        if(taskType.equals("端口扫描")){
            return Result.success("查询成功",portScanResultService.selectPortScanResult(taskId));
        }else if(taskType.equals("弱口令扫描")) {
            return Result.success("查询成功",weakPasswordScanResultService.selectWeakPasswordScanResult(taskId));
        }else if(taskType.equals("Web漏洞扫描")){
            return Result.success("查询成功",webVulnScanResultService.selectWebVulnScanResult(taskId));
        }else {
            return Result.error("400","不支持该任务类型");
        }

    }


    /**
     * 获取基本任务列表
     * @return
     */
    @GetMapping("/task/baseScanTask/list")
    public Result selectBaseScanTaskList()
    {
        String userId = SessionUtil.getUserId();
        List<BaseScanTask> baseScanTasks = baseScanTaskService.selectBaseScanTaskList(userId);
        return Result.success("查询成功",baseScanTasks);
    }




    /**
     * 添加端口扫描任务, 返回任务ID
     * @param portScanTask
     * @return
     */

    @PostMapping("/task/portScanTask")
    public Result addPortScanTask(@RequestBody PortScanTask portScanTask)
    {
        if(baseScanTaskService.checkTaskNameExists(portScanTask.getTaskName()))
        {
            return Result.error("400","任务名称已存在");
        }
        String taskId = null;
        try {
            String userId = SessionUtil.getUserId();
            portScanTask.setCreatorId(userId);
            String userName = SessionUtil.getUserName();
            portScanTask.setCreator(userName);
            taskId = portScanTaskService.addPortScanTask(portScanTask);
            return Result.success("创建成功",taskId);
        } catch (PersistenceException e) {
            KuralLogger.logErrorDetails(portScanTask,e);
            return Result.error("400","任务创建失败");
        }catch (Exception e) {
            return Result.error("400","任务创建失败");
        }


    }

    /**
     * 添加端口任务扫描结果
     * @param portScanResultVo
     * @return
     */
    @PostMapping("/task/portScanResult")
    public Result addPortScanResult(@RequestBody PortScanResultVo portScanResultVo){
        portScanResultService.addPortScanResult(portScanResultVo);
        return Result.success("端口扫描结果添加成功");
    }



    /**
     * 添加弱口令扫描任务, 返回任务ID
     * @param weakPasswordScanTask
     * @return
     */

    @PostMapping("/task/weakPasswordScanTask")
    public Result addWeakPasswordScanTask(@RequestBody WeakPasswordScanTask weakPasswordScanTask)
    {
        if(baseScanTaskService.checkTaskNameExists(weakPasswordScanTask.getTaskName()))
        {
            return Result.error("400","任务名称已存在");
        }
        //先检查任务名是否重复
        String taskId = null;
        try {
            String userId = SessionUtil.getUserId();
            weakPasswordScanTask.setCreatorId(userId);
            String userName = SessionUtil.getUserName();
            weakPasswordScanTask.setCreator(userName);
            taskId = weakPasswordScanTaskService.addWeakPasswordScanTask(weakPasswordScanTask);
            return Result.success("创建成功",taskId);
        }catch (PersistenceException e) {
            KuralLogger.logErrorDetails(weakPasswordScanTask,e);
            return Result.error("400","任务创建失败");
        }catch (Exception e) {
            return Result.error("400","任务创建失败");
        }
    }

    /**
     * 添加弱口令任务扫描结果
     * @return
     */
    @PostMapping("/task/weakPasswordScanResult")
    public Result addWeakPasswordScanResult(@RequestBody WeakPasswordScanResultVo weakPasswordScanResultVo)
    {
        weakPasswordScanResultService.addWeakPasswordScanResult(weakPasswordScanResultVo);
        return Result.success("端口扫描结果添加成功");
    }

    /**
     * 添加web漏洞扫描任务, 返回任务Id
     * @param webVulnScanTask
     * @return
     */

    @PostMapping("/task/webVulnScanTask")
    public Result addWebVulnScanTask(@RequestBody WebVulnScanTask webVulnScanTask)
    {
        if(baseScanTaskService.checkTaskNameExists(webVulnScanTask.getTaskName()))
        {
            return Result.error("400","任务名称已存在");
        }
        String taskId = null;
        try {
            String userId = SessionUtil.getUserId();
            webVulnScanTask.setCreatorId(userId);
            String userName = SessionUtil.getUserName();
            webVulnScanTask.setCreator(userName);
            taskId = webVulnScanTaskService.addWebVulnScanTask(webVulnScanTask);
            return Result.success("创建成功",taskId);
        }catch (PersistenceException e) {
            KuralLogger.logErrorDetails(webVulnScanTask,e);
            return Result.error("400","任务创建失败");
        }catch (Exception e) {
            return Result.error("400","任务创建失败");
        }
    }

    /**
     * 添加web漏洞扫描任务结果
     * @param webVulnScanResultVo
     * @return
     */
    @PostMapping("/task/webVulnScanResult")
    public Result addWebVulnScanResult(@RequestBody WebVulnScanResultVo webVulnScanResultVo){
        webVulnScanResultService.addWebVulnScanResult(webVulnScanResultVo);
        return Result.success("端口扫描结果添加成功");
    }



}
