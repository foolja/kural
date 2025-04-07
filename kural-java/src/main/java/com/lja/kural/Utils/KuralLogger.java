package com.lja.kural.Utils;

import com.lja.kural.Bean.PortScanTask;
import com.lja.kural.Bean.WeakPasswordScanTask;
import com.lja.kural.Bean.WebVulnScanTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.exceptions.PersistenceException;

import java.sql.SQLException;
@Slf4j
public class KuralLogger {

    public static void logErrorDetails(WeakPasswordScanTask task, PersistenceException e) {
        Throwable cause = e.getCause();
        if (cause instanceof SQLException) {
            SQLException sqlEx = (SQLException) cause;
            String errorMsg = sqlEx.getMessage();
            // 根据错误消息分类记录日志
            if (errorMsg.contains("duplicate key")) {
                log.error("唯一约束冲突: TaskId={}, 表=base_scan_task/weak_password_scan_task", task.getTaskId(), e);
            } else if (errorMsg.contains("cannot be null")) {
                log.error("空值约束冲突: TaskId={}, 表=base_scan_task/weak_password_scan_task", task.getTaskId(), e);
            } else {
                log.error("SQL错误: TaskId={}", task.getTaskId(), e);
            }
        } else {
            log.error("持久化异常: TaskId={}", task.getTaskId(), e);
        }
    }
    public static void logErrorDetails(PortScanTask task, PersistenceException e) {
        Throwable cause = e.getCause();
        if (cause instanceof SQLException) {
            SQLException sqlEx = (SQLException) cause;
            String errorMsg = sqlEx.getMessage();
            // 根据错误消息分类记录日志
            if (errorMsg.contains("duplicate key")) {
                log.error("唯一约束冲突: TaskId={}, 表=base_scan_task/port_scan_task,", task.getTaskId(), e);
            } else if (errorMsg.contains("cannot be null")) {
                log.error("空值约束冲突: TaskId={}, 表=base_scan_task/port_scan_task", task.getTaskId(), e);
            } else {
                log.error("SQL错误: TaskId={}", task.getTaskId(), e);
            }
        } else {
            log.error("持久化异常: TaskId={}", task.getTaskId(), e);
        }
    }
    public static void logErrorDetails(WebVulnScanTask task, PersistenceException e) {
        Throwable cause = e.getCause();
        if (cause instanceof SQLException) {
            SQLException sqlEx = (SQLException) cause;
            String errorMsg = sqlEx.getMessage();
            // 根据错误消息分类记录日志
            if (errorMsg.contains("duplicate key")) {
                log.error("唯一约束冲突: TaskId={}, 表=base_scan_task/web_vuln_scan_task", task.getTaskId(), e);
            } else if (errorMsg.contains("cannot be null")) {
                log.error("空值约束冲突: TaskId={}, 表=base_scan_task/web_vuln_scan_task", task.getTaskId(), e);
            } else {
                log.error("SQL错误: TaskId={}", task.getTaskId(), e);
            }
        } else {
            log.error("持久化异常: TaskId={}", task.getTaskId(), e);
        }
    }
}
