package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    void insertBatch(List<DishFlavor> dishFlavor);

    @Delete("delete from dish_flavor where dish_id=#{dish_id}")
    void deleteByDishId(Long dish_id);

    void deleteByDishIds(List<Long> ids);

    @Select("select * from dish_flavor where dish_id=#{dish_id}")
    List<DishFlavor> getFlavorByDishId(Long dishId);


}
