package com.lja.kural.Utils;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

public class SessionUtil {
    public static String getUserId()
    {
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        String userId =(String)session.getAttribute("userId");
        return userId;
    }
    public static String getUserName()
    {
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        String username =(String)session.getAttribute("username");
        return username;
    }
}
