package com.example.cinema.interceptor;

import com.example.cinema.config.InterceptorConfiguration;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author huwen
 * @date 2019/3/23
 */
public class SessionInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception{
        HttpSession session = httpServletRequest.getSession();

        // 1. 如果已登录，直接放行
        if(null != session && null != session.getAttribute(InterceptorConfiguration.SESSION_KEY)){
            return true;
        }

        // 2. 如果未登录，返回 JSON 格式的错误提示，而不是抛出异常或重定向 HTML
        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 设置 HTTP 状态码为 401 (未授权)
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        httpServletResponse.getWriter().write("{\"success\":false,\"message\":\"未登录或登录已过期，请重新登录！\"}");

        return false;
    }
}