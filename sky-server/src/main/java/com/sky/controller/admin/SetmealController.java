package com.sky.controller.admin;

import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageDTO;
import com.sky.enumeration.OperationType;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetMealService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "套餐管理接口")
@RequestMapping("/admin/setmeal")
@Slf4j
public class SetmealController {

    @Resource
    private SetMealService setMealService;

    @PostMapping
    @Operation(summary = "新增套餐")
    @AutoFill(OperationType.INSERT)
    @CacheEvict(cacheNames = "setmealCache",key="#setmealDTO.categoryId")
    public Result save(@RequestBody SetmealDTO setmealDTO) {
        setMealService.saveWithDish(setmealDTO);
        return Result.success();
    }


    @GetMapping("/page")
    @Operation(summary = "分页查询套餐")
    public Result<PageResult> page(SetmealPageDTO setmealPageDTO) {
        PageResult pageResult=setMealService.pageQuery(setmealPageDTO);
        return Result.success(pageResult);
    }


    @DeleteMapping
    @Operation(summary = "批量删除套餐")
    @CacheEvict(cacheNames = "setmealCache",allEntries=true)
    public Result delete(@RequestParam List<Long> ids) {
        setMealService.deleteBatch(ids);
        return Result.success();
    }

    @PutMapping
    @Operation(summary = "修改套餐")
    @AutoFill(OperationType.UPDATE)
    @CacheEvict(cacheNames = "setmealCache",allEntries=true)
    public Result update(@RequestBody SetmealDTO setmealDTO) {
        setMealService.update(setmealDTO);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @Operation(summary = "修改套餐状态")
    @AutoFill(OperationType.UPDATE)
    @CacheEvict(cacheNames = "setmealCache",allEntries=true)
    public Result updateStatus(@PathVariable Integer status,Long id) {
        setMealService.startOrStop(status,id);
        return Result.success();
    }
}
