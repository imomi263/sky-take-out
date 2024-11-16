package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

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




    List<Setmeal> list(Setmeal setmeal);


    @Select("select sd.name,sd.copies,d.image,d.description "+
            "from setmeal_dish sd left join dish d on sd.dish_id=d.id"+
            "where sd.setmeal_id =#{setmealId}")
    List<DishItemVO> getDishItemBySetmealId(Long id);

    @AutoFill(OperationType.INSERT)
    void insertSetmeal(Setmeal setmeal);


    Page<SetmealVO> pageQuery(SetmealPageDTO setmealPageDTO);

    @Select("select * from setmeal where id=#{id}")
    Setmeal getSetmealById(Long id);

    void deleteByIds(List<Long> ids);


    @Update("update setmeal set status=#{status} where id=#{id}")
    void startOrStop(Integer status, Long id);

    @AutoFill(OperationType.UPDATE)
    void update(Setmeal setmeal);

    Integer countByMap(Map map);
}
