package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageDTO;
import com.sky.entity.Dish;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@Tag(name="菜品相关")
@Slf4j
@RequestMapping("/admin/dish")
public class DishController {
    @Resource
    private DishService dishService;

    @Resource
    private RedisTemplate redisTemplate;

    @PostMapping
    @Operation(summary = "新增菜品")
    public Result save(@RequestBody DishDTO dish) {
        log.info("新增菜品:{}",dish);
        dishService.saveDishWithFlavor(dish);

        // 清理缓存
        String key="dish_"+dish.getCategoryId();
        //redisTemplate.delete(key);
        clearCache(key);


        return Result.success(dish);
    }

    @GetMapping("/page")
    @Operation(summary = "菜品分页查询")
    public Result<PageResult> pageQuery(DishPageDTO dishPageDTO) {
        log.info("菜品分类查询:{}",dishPageDTO);
        PageResult pageResult= dishService.pageQuery(dishPageDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    @Operation(summary = "菜品批量删除")
    public Result delete(@RequestParam List<Long> ids){
        log.info("菜品批量删除：{}",ids);
        dishService.deleteDishBatch(ids);

        // 找到所有dish开头的缓存
        //Set keys=redisTemplate.keys("dish_*");
        //redisTemplate.delete(keys);
        clearCache("dish_*");

        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据id查询菜品")
    public Result<DishVO> getDishVOById(@PathVariable Long id) {
        DishVO dishVO=dishService.getDishVOById(id);
        return Result.success(dishVO);
    }


    @PutMapping("/update")
    @Operation(summary = "修改菜品")
    public Result updateDish(@RequestBody DishDTO dishDTO) {
        dishService.updateDishWithFlavor(dishDTO);

        //Set keys=redisTemplate.keys("dish_*");
        //redisTemplate.delete(keys);

        clearCache("dish_*");

        return Result.success();
    }

    private void clearCache(String pattern){
        Set keys=redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}
