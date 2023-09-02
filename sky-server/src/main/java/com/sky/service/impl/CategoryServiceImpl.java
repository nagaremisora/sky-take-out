package com.sky.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.annotation.AutoFill;
import com.sky.constant.MessageConstant;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import com.sky.mapper.CategoryMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import com.sky.service.EmployeeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    /**
     * 分页查询
     * @param pageQueryDTO
     * @return
     */
    @Override
    public Result<PageResult> getAll(CategoryPageQueryDTO pageQueryDTO) {
        IPage page = query().like(StrUtil.isNotBlank(pageQueryDTO.getName()), "name", pageQueryDTO.getName())
                .eq(pageQueryDTO.getType() != null, "type", pageQueryDTO.getType())
                .orderByAsc("sort").page(new Page<>(pageQueryDTO.getPage(), pageQueryDTO.getPageSize()));
        List records = page.getRecords();
        int total = query().like(StrUtil.isNotBlank(pageQueryDTO.getName()), "name", pageQueryDTO.getName())
                .eq(pageQueryDTO.getType() != null, "type", pageQueryDTO.getType()).list().size();
        return Result.success(new PageResult(total, records));
    }

    /**
     * 新增分类
     * @param category
     * @return
     */
    @Override
    @AutoFill(OperationType.INSERT)
    public Result addCategory(Category category) {
        if(query().eq("name", category.getName()).one() != null) {
            return Result.error(MessageConstant.CATEGORY_EXIST);
        }
        if(query().eq("sort", category.getSort()).one() != null) {
            return Result.error(MessageConstant.CATEGORY_EXIST_BY_SORT);
        }
        category.setStatus(1);
        save(category);
        return Result.success();
    }

    /**
     * 更新分类
     * @param category
     * @return
     */
    @AutoFill(OperationType.UPDATE)
    @Override
    public Result updateCategory(Category category) {
        Category c1 = query().eq("name", category.getName()).one();
        if(c1 != null && c1.getId() != category.getId()) {
            return Result.error(MessageConstant.CATEGORY_EXIST);
        }
        Category c2 = query().eq("sort", category.getSort()).one();
        if(c2 != null && c2.getId() != category.getId()) {
            return Result.error(MessageConstant.CATEGORY_EXIST_BY_SORT);
        }
        updateById(category);
        return Result.success();
    }

    /**
     * 启用禁用分类
     * @param category
     * @return
     */
    @AutoFill(OperationType.UPDATE)
    @Override
    public Result onOffCategory(Category category) {
        updateById(category);
        return Result.success();
    }
}
