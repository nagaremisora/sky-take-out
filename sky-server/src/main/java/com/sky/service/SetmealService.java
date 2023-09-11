package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    Result<List<Setmeal>> getListByCategoryId(Long categoryId);

    List<DishItemVO> getDishBySetmealId(Long id);

    SetmealVO getBySetId(Long id);

    PageResult getSetmealByPage(SetmealPageQueryDTO dto);
}
