package com.sky.service.Impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Resource
    private DishMapper dishMapper;

    @Resource
    private DishFlavorMapper dishFlavorMapper;

    @Override
    @Transactional // 要么全失败要么全成功
    public void saveDishWithFlavor(DishDTO dishDTO) {

        // 向菜品插入1条数据
        Dish dish=new Dish();

        BeanUtils.copyProperties(dishDTO,dish);

        dishMapper.insertDish(dish);
        // 向口味表插入n条数据

        Long dishId=dish.getId();

        List<DishFlavor> flavors=dish.getFlavors();
        log.info("新增品味：{}",flavors);

        if(flavors!=null && !flavors.isEmpty()){
            flavors.forEach(flavor->{
               flavor.setDishId(dishId);
            });
            // 向口味表插入n条数据
            dishFlavorMapper.insertBatch(flavors);
        }
    }
}
