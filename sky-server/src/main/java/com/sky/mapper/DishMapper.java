package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */

    @Operation(summary = "查询菜品数量")
    @Select("select count(id) from dish where category_id=#{categoryId}")
    Integer countByCategoryId(Long categoryId);


    @Operation(summary = "插入菜品")
    @AutoFill(value= OperationType.INSERT)
    void insertDish(Dish dish);

}
