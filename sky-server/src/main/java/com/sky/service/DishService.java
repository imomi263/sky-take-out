package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    public void saveDishWithFlavor(DishDTO dishDTO);


    PageResult pageQuery(DishPageDTO dishPageDTO);

    void deleteDishBatch(List<Long> ids);

    DishVO getDishVOById(Long id);

    void updateDishWithFlavor(DishDTO dishDTO);

    List<DishVO> listWithFlavor(Dish dish);
}
