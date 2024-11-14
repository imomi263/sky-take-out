package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;

import java.util.List;

public interface SetMealService {

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    List<DishItemVO> getDishItemById(Long id);

    void saveWithDish(SetmealDTO setmealDTO);

    PageResult pageQuery(SetmealPageDTO setmealPageDTO);

    void deleteBatch(List<Long> ids);

    void startOrStop(Integer status, Long id);

    void update(SetmealDTO setmealDTO);
}
