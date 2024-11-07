package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name="菜品相关")
@Slf4j
@RequestMapping("/admin/dish")
public class DishController {
    @Resource
    private DishService dishService;


    @PostMapping
    @Operation(summary = "新增菜品")
    public Result save(@RequestBody DishDTO dish) {
        log.info("新增菜品:{}",dish);
        dishService.saveDishWithFlavor(dish);
        return Result.success(dish);
    }
}
