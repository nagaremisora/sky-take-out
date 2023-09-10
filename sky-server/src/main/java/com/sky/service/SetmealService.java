package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.vo.DishItemVO;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    Result<List<Setmeal>> getListByCategoryId(Long categoryId);

    Result<List<DishItemVO>> getDishBySetmealId(Long id);
}
