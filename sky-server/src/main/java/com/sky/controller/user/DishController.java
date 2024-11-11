package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
@Tag(name="C端-菜品浏览接口")
public class DishController {

    @Resource
    private DishService dishService;

    @Resource
    private RedisTemplate redisTemplate;

    @GetMapping("/list")
    @Operation(summary = "根据分类id查询菜品")
    public Result<List<DishVO>> list(Long categoryId) {



        // 查询redis中是否存在菜品数据
        String key="dish_"+categoryId;

        List<DishVO> r_list=(List<DishVO>) redisTemplate.opsForValue().get(key);
        if(r_list!=null && r_list.size()>0){
            return Result.success(r_list);
        }

        // 不存在的话，就去数据库查然后加入缓存中

        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);//查询起售中的菜品

        List<DishVO> list = dishService.listWithFlavor(dish);

        //然后加入缓存中
        redisTemplate.opsForValue().set(key,list);

        return Result.success(list);
    }

}
