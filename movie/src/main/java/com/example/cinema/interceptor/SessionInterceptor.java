package com.example.cinema.interceptor;

import com.example.cinema.config.InterceptorConfiguration;
import com.example.cinema.vo.UserVO;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;

/**
 * @author huwen
 * @date 2019/3/23
 */
public class SessionInterceptor extends HandlerInterceptorAdapter {
    // 管理员接口路径前缀
    private static final List<String> ADMIN_PATHS = Arrays.asList(
        "/management/",
        "/admin/",
        "/delete/",
        "/insert/",
        "/update/"
    );
    
    // 普通用户接口路径前缀
    private static final List<String> USER_PATHS = Arrays.asList(
        "/user/",
        "/ticket/",
        "/payment/",
        "/vip/",
        "/coupon/"
    );
    
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception{
        HttpSession session = httpServletRequest.getSession();
        
        // 1. 检查是否登录
        if(null == session || null == session.getAttribute(InterceptorConfiguration.SESSION_KEY)){
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpServletResponse.setContentType("application/json;charset=UTF-8");
            httpServletResponse.getWriter().write("{\"success\":false,\"message\":\"未登录或登录已过期，请重新登录！\"}");
            return false;
        }
        
        // 2. 获取用户信息
        UserVO user = (UserVO) session.getAttribute(InterceptorConfiguration.SESSION_KEY);
        String requestURI = httpServletRequest.getRequestURI();
        
        // 3. 检查管理员权限
        boolean isAdminPath = ADMIN_PATHS.stream().anyMatch(requestURI::contains);
        boolean isUserPath = USER_PATHS.stream().anyMatch(requestURI::contains);
        
        // 管理员接口需要kind=1
        if (isAdminPath && user.getKind() != 1) {
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpServletResponse.setContentType("application/json;charset=UTF-8");
            httpServletResponse.getWriter().write("{\"success\":false,\"message\":\"权限不足，需要管理员权限！\"}");
            return false;
        }
        
        // 普通用户接口需要kind=2
        if (isUserPath && user.getKind() != 2) {
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpServletResponse.setContentType("application/json;charset=UTF-8");
            httpServletResponse.getWriter().write("{\"success\":false,\"message\":\"权限不足，需要普通用户权限！\"}");
            return false;
        }
        
        // 4. 检查用户只能操作自己的数据
        String userIdParam = httpServletRequest.getParameter("userId");
        if (userIdParam != null) {
            int requestUserId = Integer.parseInt(userIdParam);
            if (requestUserId != user.getId() && user.getKind() != 1) {
                httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                httpServletResponse.setContentType("application/json;charset=UTF-8");
                httpServletResponse.getWriter().write("{\"success\":false,\"message\":\"只能操作自己的数据！\"}");
                return false;
            }
        }
        
        return true;
    }
}