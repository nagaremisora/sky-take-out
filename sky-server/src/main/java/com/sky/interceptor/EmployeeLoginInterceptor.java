package com.sky.interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.sky.constant.RedisConstant;
import com.sky.utils.EmployeeHolder;
import com.sky.vo.EmployeeLoginVO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class EmployeeLoginInterceptor implements HandlerInterceptor {
    private StringRedisTemplate redisTemplate;
    public EmployeeLoginInterceptor(StringRedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Token");
        if(StrUtil.isBlank(token)){
            response.setStatus(401);
            return false;
        }
        String tokenKey = RedisConstant.EMPLOYEE_LOGIN_TOKEN + token;
        Map<Object, Object> employeeMap = redisTemplate.opsForHash().entries(tokenKey);
        if(employeeMap.isEmpty()){
            response.setStatus(401);
            return false;
        }
        EmployeeHolder.saveUser(BeanUtil.fillBeanWithMap(employeeMap, new EmployeeLoginVO(), false));
        redisTemplate.expire(tokenKey, RedisConstant.EMPLOYEE_LOGIN_TOKEN_TTL, TimeUnit.MINUTES);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        EmployeeHolder.removeUser();
    }
}
