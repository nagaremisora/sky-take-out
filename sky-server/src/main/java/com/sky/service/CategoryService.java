package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;

public interface CategoryService extends IService<Category> {
    Result<PageResult> getAll(CategoryPageQueryDTO pageQueryDTO);

    Result addCategory(Category category);

    Result updateCategory(Category category);

    Result onOffCategory(Category category);
}
