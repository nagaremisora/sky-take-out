package com.sky.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.constant.RedisConstant;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetmealMapper;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.service.SetmealDishService;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private DishService dishService;

    /**
     * 根据分类id查套餐
     * @param categoryId
     * @return
     */
    @Override
    public Result<List<Setmeal>> getListByCategoryId(Long categoryId) {
        String key = RedisConstant.SETMEAL_BY_CATEGORY_ID + categoryId;
        Set<String> members = redisTemplate.opsForSet().members(key);

        List<Setmeal> setmealList = new ArrayList<>();

        if (members == null || members.size() == 0) {
            // 缓存中不存在走数据库
            setmealList = query().eq("category_id", categoryId).eq("status", 1).list();
            setmealList.forEach(e -> {
                redisTemplate.opsForSet().add(key, JSONUtil.toJsonStr(e));
                redisTemplate.expire(key, 5L, TimeUnit.MINUTES);
            });
        }else {
            setmealList = members.stream().map(i -> JSONUtil.toBean(i, Setmeal.class)).collect(Collectors.toList());
        }
        return Result.success(setmealList);
    }

    @Override
    public Result<List<DishItemVO>> getDishBySetmealId(Long id) {
        List<SetmealDish> setmealDishList = setmealDishService.query().eq("setmeal_id", id).list();
        List<DishItemVO> dishItemList = new ArrayList<>();
        List<Dish> dishList = dishService.listByIds(setmealDishList.stream().map(i -> i.getDishId()).collect(Collectors.toList()));
        for (Dish dish : dishList) {
            for (SetmealDish setmealDish : setmealDishList) {
                if(setmealDish.getDishId() == dish.getId()) {
                    DishItemVO dishItemVO = DishItemVO.builder().name(setmealDish.getName()).copies(setmealDish.getCopies()).image(dish.getImage()).description(dish.getDescription()).build();
                    dishItemList.add(dishItemVO);
                }
            }
        }
        return Result.success(dishItemList);
    }
}
