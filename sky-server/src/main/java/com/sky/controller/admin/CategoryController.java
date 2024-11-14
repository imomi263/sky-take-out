package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryTypePageDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.ReportAsSingleViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
@Tag(name="分类相关")
@Slf4j
public class CategoryController {
    @Resource
    private CategoryService categoryService;

    @PostMapping
    @Operation(summary = "新增分类")
    //@CachePut(cacheNames="userCache",key="#user.id")
    // key的生成是  userCache::xxxx
    public Result<String> save(@RequestBody CategoryDTO categoryDTO){
        log.info("新增分类：{}", categoryDTO);
        categoryService.save(categoryDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @Operation(summary = "分类分页查询")
    public Result<PageResult> page(CategoryTypePageDTO  categoryTypePageDTO){
        log.info("分页查询：{}", categoryTypePageDTO);
        PageResult pageResult = categoryService.pageQuery(categoryTypePageDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    @Operation(summary = "删除分类")
    public Result<String> deleteById(Long id){
        log.info("删除分类：{}", id);
        categoryService.deleteById(id);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @Operation(summary = "启用禁用分类")
    public Result<String> startOrStop(@PathVariable("status") Integer status, Long id){
        categoryService.startOrStop(status,id);
        return Result.success();
    }

    @GetMapping("/list")
    @Operation(summary ="根据类型查询分类")
    public Result<List<Category>> list(Integer type){
        List<Category> list = categoryService.list(type);
        return Result.success(list);
    }

}
