package com.sky.service.Impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.annotation.AutoFill;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import com.sky.exception.DeleteNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetMealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class SetMealServiceImpl implements SetMealService {
    @Autowired
    private SetMealMapper setmealMapper;
    @Autowired
    private SetMealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetMealDishMapper setMealDishMapper;

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }

    //@AutoFill(value= OperationType.INSERT)
    @Override
    @Transactional
    public void saveWithDish(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        setmealMapper.insertSetmeal(setmeal);

        Long setmealId= setmeal.getId();
        List<SetmealDish> dishes=setmealDTO.getSetmealDishes();
        if(dishes!=null && dishes.size()>0){
            dishes.forEach(dish -> {
                dish.setSetmealId(setmealId);
            });
        }
        setmealDishMapper.insertBatch(dishes);
    }

    @Override
    public PageResult pageQuery(SetmealPageDTO setmealPageDTO) {
        PageHelper.startPage(setmealPageDTO.getPage(),setmealPageDTO.getPageSize());
        Page<SetmealVO> page=setmealMapper.pageQuery(setmealPageDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void deleteBatch(List<Long> ids) {
        for(Long id:ids){
            Setmeal setmeal=setmealMapper.getSetmealById(id);
            if(setmeal.getStatus()==1){
                throw new DeleteNotAllowedException(
                        MessageConstant.SETMEAL_ON_SALE);
            }
        }
        setmealMapper.deleteByIds(ids);
        setmealDishMapper.deleteByIds(ids);
    }

    @AutoFill(value= OperationType.UPDATE)
    @Override
    public void startOrStop(Integer status, Long id) {
        setmealMapper.startOrStop(status,id);

    }

    @Override
    //@AutoFill(value= OperationType.UPDATE)
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal=new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        setmealMapper.update(setmeal);

        setMealDishMapper.deleteById(setmeal.getId());

        List<SetmealDish> flavors=setmealDTO.getSetmealDishes();

        if(flavors!=null && !flavors.isEmpty()){
            flavors.forEach(flavor->{
                flavor.setDishId(setmeal.getId());
            });
            // 向口味表插入n条数据
            setMealDishMapper.insertBatch(flavors);
        }
    }
}
