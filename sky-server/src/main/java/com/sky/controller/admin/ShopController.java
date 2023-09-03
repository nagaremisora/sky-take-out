package com.sky.controller.admin;

import cn.hutool.core.util.StrUtil;
import com.sky.constant.RedisConstant;
import com.sky.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/shop")
public class ShopController {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @PutMapping("/{status}")
    public Result setStatus(@PathVariable Integer status) {
        redisTemplate.opsForValue().set(RedisConstant.SHOP_STATUS, status.toString());
        return Result.success();
    }

    @GetMapping("/status")
    public Result<Integer> getStatus() {
        String status = redisTemplate.opsForValue().get(RedisConstant.SHOP_STATUS);
        if(StrUtil.isEmpty(status)){
            redisTemplate.opsForValue().set(RedisConstant.SHOP_STATUS, "1");
            status = "1";
        }
        return Result.success(Integer.parseInt(status));
    }
}
