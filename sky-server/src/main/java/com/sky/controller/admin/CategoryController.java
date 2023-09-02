package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 分类分页查询
     * @param pageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> getAllByPage(CategoryPageQueryDTO pageQueryDTO){
        return categoryService.getAll(pageQueryDTO);
    }

    /**
     * 新增分类
     * @param categoryDTO
     * @return
     */
    @PostMapping
    public Result addCategory(@RequestBody CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        return categoryService.addCategory(category);
    }

    /**
     * 修改菜品
     * @param categoryDTO
     * @return
     */
    @PutMapping
    public Result updateCategory(@RequestBody CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        return categoryService.updateCategory(category);
    }

    /**
     * 启用禁用分类
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    public Result onOffCategory(@PathVariable("status") Integer status, @RequestParam("id") Long id) {
        Category category = categoryService.getById(id);
        category.setStatus(status);
        return categoryService.onOffCategory(category);
    }

    /**
     * 删除分类
     * @param id
     * @return
     */
    @DeleteMapping
    public Result deleteCategory(Long id){
        categoryService.removeById(id);
        return Result.success();
    }

    /**
     * 根据类型分类
     * @param type
     * @return
     */
    @GetMapping("/list")
    public Result<List<Category>> getListByType(Integer type) {
        List<Category> list = categoryService.query().eq("type", type).list();
        return Result.success(list);
    }
}
