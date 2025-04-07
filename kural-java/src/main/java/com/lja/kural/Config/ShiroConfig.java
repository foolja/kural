package com.lja.kural.Config;

import com.lja.kural.Component.UserRealm;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.MemorySessionDAO;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {
    @Autowired
    private UserRealm userRealm;
    @Bean
    public SessionDAO sessionDAO() {
        return new MemorySessionDAO(); // 默认内存存储，可替换为其他实现
    }
    @Bean
    public SimpleCookie simpleCookie() {
        SimpleCookie simpleCookie = new SimpleCookie("sessionId");
        simpleCookie.setHttpOnly(false); // 取消 HTTP-only 标志
        return simpleCookie;
    }
    @Bean
    public SessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        // 为了解决输入网址地址栏出现 jsessionid 的问题
        sessionManager.setSessionIdUrlRewritingEnabled(false);
        sessionManager.setSessionDAO(sessionDAO());
        sessionManager.setSessionIdCookie(simpleCookie());
        sessionManager.setGlobalSessionTimeout(1800000); // 30分钟（单位：毫秒）
        // 是否开启删除无效的session对象  默认为true
        sessionManager.setDeleteInvalidSessions(true);
        // 是否开启定时调度器进行检测过期session 默认为true
        sessionManager.setSessionValidationSchedulerEnabled(true);
        return sessionManager;
    }
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager)
    {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        factoryBean.setSecurityManager(securityManager);
        // 设置登录页面路径，如果接受的请求没有认证信息(sessionId),则直接内部转发到/api/v1/login-tip
        factoryBean.setLoginUrl("/api/v1/login-tip");
        // 定义过滤器
        Map<String, String> filterChainDefinitionMap = new HashMap<>();
//        filterChainDefinitionMap.put("/api/v1/login", "anon");
//        filterChainDefinitionMap.put("/api/v1/logout", "anon");
//        filterChainDefinitionMap.put("/task-progress/**", "anon");
//        filterChainDefinitionMap.put("/api/v1/result/**", "anon");
//        filterChainDefinitionMap.put("/**", "authc");  // 所有其他请求都需要登录
        filterChainDefinitionMap.put("**", "anon");
        factoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return factoryBean;
    }
    @Bean
    public DefaultWebSecurityManager securityManager()
    {
        DefaultWebSecurityManager securityManager  = new DefaultWebSecurityManager();
        securityManager.setRealm(userRealm);
        securityManager.setSessionManager(sessionManager());
        return securityManager;
    }

}
