package com.sky.controller.user;

import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/setmeal")
public class UserSetMealController {
    @Autowired
    private SetmealService service;

    @GetMapping("/list")
    public Result<List<Setmeal>> getListByCategoryId(@RequestParam Long categoryId) {
        return service.getListByCategoryId(categoryId);
    }

    @GetMapping("/dish/{id}")
    public Result<List<DishItemVO>> getDishById(@PathVariable Long id) {
        return service.getDishBySetmealId(id);
    }
}
