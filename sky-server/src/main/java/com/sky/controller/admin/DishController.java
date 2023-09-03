package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.ReferenceUriSchemesSupported;
import java.util.List;

@RestController
@RequestMapping("/admin/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    /**
     * 分页查询
     * @param dto
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> getDishByPage(DishPageQueryDTO dto) {
        return dishService.getDishByPage(dto);
    }

    /**
     * 根据ID查菜品
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<DishVO> getDishById(@PathVariable Long id) {
        return dishService.getDishById(id);
    }

    /**
     * 根据分类ID查菜品
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    public Result<List<Dish>> getDishByType(Long categoryId) {
        return Result.success(dishService.query().eq("category_id", categoryId).list());
    }

    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping
    public Result addDish(@RequestBody DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        return dishService.addDish(dish, dishDTO.getFlavors());
    }

    /**
     * 修改商品状态
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    public Result updateStatus(@PathVariable("status") Integer status, @RequestParam("id") Long id) {
        Dish dish = dishService.getById(id);
        dish.setStatus(status);
        return dishService.updateStatus(dish);
    }

    /**
     * 修改菜品
     * @param dishVO
     * @return
     */
    @PutMapping
    public Result updateDish(@RequestBody DishVO dishVO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishVO, dish);
        return dishService.updateDish(dish, dishVO.getFlavors());
    }

    @DeleteMapping
    public Result deleteDish(@RequestParam List<Long> ids) {
        System.out.println(ids);
        dishService.removeByIds(ids);
        return Result.success();
    }
}
