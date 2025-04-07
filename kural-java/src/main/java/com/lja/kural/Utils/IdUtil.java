package com.lja.kural.Utils;

import cn.hutool.core.lang.Snowflake;

public class IdUtil {

    private static final Snowflake snowflakeForTask;
    private static final Snowflake snowflakeForUser;

    static {
        //参数1：workerId 终端ID, 从环境变量或配置文件中获取
        //参数2：dataCenterId 数据中心ID, 从环境变量或配置文件中获取
        long workerIdForTask = 1;
        long dataCenterIdForTask = 1;
        long workerIdForUser = 2;
        long dataCenterIdForUser = 2;
        snowflakeForTask = new Snowflake(dataCenterIdForTask, workerIdForTask);
        snowflakeForUser = new Snowflake(dataCenterIdForUser,workerIdForUser);
    }

    public static String generateTaskId()
    {
        return String.valueOf(snowflakeForTask.nextId());
    }
    public static String generateUserId()
    {
        return String.valueOf(snowflakeForUser.nextId());
    }
}
