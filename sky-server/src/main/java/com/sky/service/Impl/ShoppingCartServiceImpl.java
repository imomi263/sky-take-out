package com.sky.service.Impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Resource
    private ShoppingCartMapper shoppingCartMapper;

    @Resource
    private DishMapper dishMapper;

    @Resource
    private SetMealMapper setmealMapper;

    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        // 判断当前加入购物车里的商品是否已经存在了

        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        log.info("userId:{}",BaseContext.getCurrentId());
        List<ShoppingCart> list= shoppingCartMapper.listShoppingCart(shoppingCart);

        // 如果已经存在了就数量加一
        if(list!=null && list.size()>0){
            ShoppingCart cart=list.get(0);
            cart.setNumber(cart.getNumber()+1);
            shoppingCartMapper.updateNumberById(cart);
        }else{
            // 如果不存在，需要插入一条购物车数据
            // 判断是否为空
            Long dishId=shoppingCartDTO.getDishId();
            if(dishId!=null){
                // 添加的是菜品
                Dish dish=dishMapper.getDishById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());

            }else{
                // 套餐
                Setmeal setmeal=setmealMapper.getSetmealById(shoppingCartDTO.getSetmealId());
                //Long setmealId=shoppingCartDTO.getSetmealId();

                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());

            }


            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insertOne(shoppingCart);


        }

    }


    @Override
    public List<ShoppingCart> showShoppingCart() {
        Long userId=BaseContext.getCurrentId();
        ShoppingCart shoppingCart=ShoppingCart.builder()
                .userId(userId)
                .build();
        List<ShoppingCart> list=shoppingCartMapper.listShoppingCart(shoppingCart);
        return list;
    }


    @Override
    public void cleanShoppingCart() {
        Long userId=BaseContext.getCurrentId();
        shoppingCartMapper.deleteByUserId(userId);

    }
}
