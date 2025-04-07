package com.lja.kural.Config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {
    // 定义 CORS 规则
    private CorsConfiguration buildCorsConfig() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // 允许携带 Cookie
        config.addAllowedOrigin("http://127.0.0.1:8080"); // 允许的域名
        config.addAllowedOrigin("http://127.0.0.1:9999");
        config.addAllowedMethod("*"); // 允许所有方法（GET/POST等）
        config.addAllowedHeader("*"); // 允许所有请求头
        return config;
    }

    // 注册 CorsFilter 并设置最高优先级
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", buildCorsConfig()); // 对所有路径生效

        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE); // 确保优先级高于 Shiro
        return bean;
    }
}
