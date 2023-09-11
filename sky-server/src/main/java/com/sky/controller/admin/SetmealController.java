package com.sky.controller.admin;

import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @GetMapping("/{id}")
    public Result<SetmealVO> getBySetId(@PathVariable Long id) {
        return Result.success(setmealService.getBySetId(id));
    }

    @GetMapping("/page")
    public Result<PageResult> getSetmealByPage(@RequestParam SetmealPageQueryDTO dto) {
        return Result.success(setmealService.getSetmealByPage(dto));
    }
}
