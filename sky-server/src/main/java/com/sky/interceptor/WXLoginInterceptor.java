package com.sky.interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.sky.constant.RedisConstant;
import com.sky.utils.EmployeeHolder;
import com.sky.utils.UserHolder;
import com.sky.vo.UserLoginVO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class WXLoginInterceptor implements HandlerInterceptor {
    private StringRedisTemplate redisTemplate;
    public WXLoginInterceptor(StringRedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("authentication");
        if(StrUtil.isBlank(token)){
            response.setStatus(401);
            return false;
        }
        String tokenKey = RedisConstant.WX_USER_TOKEN + token;
        Map<Object, Object> userMap = redisTemplate.opsForHash().entries(tokenKey);
        if(userMap.isEmpty()){
            response.setStatus(401);
            return false;
        }
        UserHolder.saveUser(BeanUtil.fillBeanWithMap(userMap, new UserLoginVO(), false));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.removeUser();
    }
}
