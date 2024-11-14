package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper

public interface SetMealDishMapper {
    /*
        根据菜品id查询套餐id
     */
    // select setmeal_id from setmeal_dish where dish_id in (1,2,3,4)
    List<Long> getSetMealIdsByCategoryId(List<Long> dishIds);


    void insertBatch(List<SetmealDish> dishes);

    void deleteByIds(List<Long> ids);

    @Delete("delete from setmeal_dish where setmeal_id = #{id}")
    void deleteById(Long id);
}
