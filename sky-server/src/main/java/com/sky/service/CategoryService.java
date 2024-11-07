package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryTypePageDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;

public interface CategoryService {

    @Operation(summary = "新增分类")
    void save(CategoryDTO categoryDTO);

    @Operation(summary = "分页查询")
    PageResult pageQuery(CategoryTypePageDTO categorytypepageDTO);

    @Operation(summary = "删除分类")
    void deleteById(Long id);

    @Operation(summary = "更新分类 ")
    void update(CategoryDTO categoryDTO);

    /**
     * 启用、禁用分类
     * @param status
     * @param id
     */
    @Operation(summary = "禁用启用分类")
    void startOrStop(Integer status, Long id);

    /**
     * 根据类型查询分类
     * @param type
     * @return
     */
    @Operation(summary = "根据类型查询分类")
    List<Category> list(Integer type);


}
