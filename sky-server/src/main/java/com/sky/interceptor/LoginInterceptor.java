package com.sky.interceptor;

import com.sky.utils.EmployeeHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(EmployeeHolder.getUser()==null){
            response.setStatus(401);
            return false;
        }
        return true;
    }
}
