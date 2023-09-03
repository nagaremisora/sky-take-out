package com.sky.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.annotation.AutoFill;
import com.sky.constant.MessageConstant;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import com.sky.service.DishFlavorService;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 分页查询
     * @param dto
     * @return
     */
    @Override
    public Result<PageResult> getDishByPage(DishPageQueryDTO dto) {
        QueryChainWrapper<Dish> qcw = query().eq(dto.getStatus() != null, "status", dto.getStatus())
                .eq(dto.getCategoryId() != null, "category_id", dto.getCategoryId())
                .like(StrUtil.isNotBlank(dto.getName()), "name", dto.getName());
        Integer count = qcw.count();
        List<Dish> records = qcw.page(new Page<>(dto.getPage(), dto.getPageSize())).getRecords();
        List<DishVO> recordsVo = new ArrayList<>();
        records.forEach(e -> {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(e, dishVO);
            recordsVo.add(dishVO);
        });
        List<Category> categoryList = categoryService.query().list();
        recordsVo.forEach(e -> {
            for (Category category : categoryList) {
                if(category.getId() == e.getCategoryId()){
                    e.setCategoryName(category.getName());
                }
            }
        });
        return Result.success(new PageResult(count, recordsVo));
    }

    /**
     * 根据ID查菜品
     * @param id
     * @return
     */
    @Override
    public Result<DishVO> getDishById(Long id) {
        Dish dish = getById(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setCategoryName(categoryService.getById(dishVO.getCategoryId()).getName());
        dishVO.setFlavors(dishFlavorService.query().eq("dish_id", dish.getId()).list());
        return Result.success(dishVO);
    }

    /**
     * 新增菜品
     * @param dish
     * @param dishFlavors
     * @return
     */
    @Override
    @AutoFill(OperationType.INSERT)
    @Transactional
    public Result addDish(Dish dish, List<DishFlavor> dishFlavors) {
        if(query().eq("name", dish.getName()).one() != null) {
            return Result.error(MessageConstant.DISH_EXIST);
        }
        dish.setStatus(1);
        save(dish);
        dishFlavors.forEach(e -> e.setDishId(dish.getId()));
        dishFlavorService.saveBatch(dishFlavors);
        return Result.success();
    }

    /**
     * 修改菜品
     * @param dish
     * @param dishFlavors
     * @return
     */
    @Override
    @Transactional
    @AutoFill(OperationType.UPDATE)
    public Result updateDish(Dish dish, List<DishFlavor> dishFlavors) {
        if(query().eq("name", dish.getName()).one() != null) {
            return Result.error(MessageConstant.DISH_EXIST);
        }
        updateById(dish);
        dishFlavorService.updateBatchById(dishFlavors);
        return Result.success();
    }

    /**
     * 修改菜品状态
     * @param dish
     * @return
     */
    @Override
    @AutoFill(OperationType.UPDATE)
    public Result updateStatus(Dish dish) {
        updateById(dish);
        return Result.success();
    }
}
