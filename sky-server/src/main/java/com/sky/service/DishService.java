package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService extends IService<Dish> {
    Result<PageResult> getDishByPage(DishPageQueryDTO dto);

    Result<DishVO> getDishById(Long id);

    Result addDish(Dish dish, List<DishFlavor> dishFlavors);

    Result updateDish(Dish dish, List<DishFlavor> dishFlavors);

    Result updateStatus(Dish dish);

    Result<List<DishVO>> getDishByCategoryId(Long categoryId);
}
