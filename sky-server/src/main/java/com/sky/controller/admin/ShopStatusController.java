package com.sky.controller.admin;

import com.sky.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/shop/status")
public class ShopStatusController {
    @GetMapping
    public Result<Integer> getShopStatus(){
        return Result.success(1);
    }
}
