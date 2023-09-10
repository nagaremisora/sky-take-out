package com.sky.controller.user;

import com.sky.constant.RedisConstant;
import com.sky.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/shop")
public class UserShopController {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("/status")
    public Result<Integer> getStatus() {
        return Result.success(Integer.parseInt(redisTemplate.opsForValue().get(RedisConstant.SHOP_STATUS)));
    }
}
