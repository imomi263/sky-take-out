package com.sky.service.Impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeleteNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Resource
    private DishMapper dishMapper;

    @Resource
    private DishFlavorMapper dishFlavorMapper;

    @Resource
    private SetMealMapper setMealMapper;
    @Autowired
    private SetMealDishMapper setMealDishMapper;

    @Override
    @Transactional // 要么全失败要么全成功
    public void saveDishWithFlavor(DishDTO dishDTO) {

        // 向菜品插入1条数据
        Dish dish=new Dish();

        BeanUtils.copyProperties(dishDTO,dish);

        dishMapper.insertDish(dish);
        // 向口味表插入n条数据

        Long dishId=dish.getId();

        List<DishFlavor> flavors=dishDTO.getFlavors();
        log.info("新增品味：{}",flavors);

        if(flavors!=null && !flavors.isEmpty()) {
            flavors.forEach(flavor -> {
                flavor.setDishId(dishId);
            });
        }
            // 向口味表插入n条数据
            dishFlavorMapper.insertBatch(flavors);

    }

    @Override
    public PageResult pageQuery(DishPageDTO dishPageDTO) {
        PageHelper.startPage(dishPageDTO.getPage(),dishPageDTO.getPageSize());
        Page<DishVO> page=dishMapper.pageQuery(dishPageDTO);
        return new PageResult(page.getTotal(),page.getResult());

    }

    @Override
    public void deleteDishBatch(List<Long> ids) {
        // 判断当前菜品是否能删除---是否存在起售中的菜品？
        for(Long id:ids){
            Dish dish=dishMapper.getDishById(id);
            if(Objects.equals(dish.getStatus(), StatusConstant.ENABLE)){
                // 当前菜品处于起售中，不能删除
                throw new DeleteNotAllowedException(
                        MessageConstant.DISH_ON_SALE);
            }
        }

        // 判断当前菜品是否能删除--是否菜品被套餐关联
        List<Long> setmealIds=setMealDishMapper.getSetMealIdsByCategoryId(ids);
        if(setmealIds!=null && !setmealIds.isEmpty()){
            // 当前菜品被关联了
            throw new DeleteNotAllowedException(
                    MessageConstant.SETMEAL_ON_SALE
            );
        }

        // 删除菜品表中的菜品数据
//        for(Long id:ids){
//            dishMapper.deleteById(id);
//            // 删除口味数据
//            dishFlavorMapper.deleteByDishId(id);
//        }
        dishMapper.deleteByIds(ids);

        dishFlavorMapper.deleteByDishIds(ids);

    }


    @Override
    public DishVO getDishVOById(Long id) {
        // 根据id查询菜品数据
        Dish dish=dishMapper.getDishById(id);
        // 根据菜品id查询口味数据
        List<DishFlavor> dishFlavor=dishFlavorMapper.getFlavorByDishId(id);
        // 将查询到的数据封装到VO
        DishVO dishVO=new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors( dishFlavor);
        return null;
    }

    @Override
    public void updateDishWithFlavor(DishDTO dishDTO) {

        Dish dish=new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.updateDish(dish);

        dishFlavorMapper.deleteByDishId(dish.getId());

        List<DishFlavor> flavors=dishDTO.getFlavors();

        if(flavors!=null && !flavors.isEmpty()){
            flavors.forEach(flavor->{
                flavor.setDishId(dish.getId());
            });
            // 向口味表插入n条数据
            dishFlavorMapper.insertBatch(flavors);
        }

    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getFlavorByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }
}
