package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

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


    Page<DishVO> pageQuery(DishPageDTO dishPageDTO);


    @Select("select * from dish where id=#{id}")
    Dish getDishById(Long id);


    @Delete("delete from dish where id=#{id}")
    void deleteById(Long id);


    void deleteByIds(List<Long> ids);


    @AutoFill(value= OperationType.UPDATE)
    void updateDish(Dish dish);



    List<Dish> list(Dish dish);
}
