package com.sky.mapper;

import com.sky.entity.Setmeal;
import com.sky.vo.DishItemVO;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetMealMapper {

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Operation(summary = "查询套餐数量")
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    /*
        根据菜品id查询套餐id
     */
    // select setmeal_id from setmeal_dish where dish_id in (1,2,3,4)
    List<Long> getSetMealIdsByCategoryId(List<Long> dishIds);


    List<Setmeal> list(Setmeal setmeal);


    @Select("select sd.name,sd.copies,d.image,d.description "+
            "from setmeal_dish sd left join dish d on sd.dish_id=d.id"+
            "where sd.setmeal_id =#{setmealId}")
    List<DishItemVO> getDishItemBySetmealId(Long id);
}
