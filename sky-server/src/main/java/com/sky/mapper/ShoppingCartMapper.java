package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    @Update("update shopping_cart set number=#{number} where id=#{id}")
    void updateNumberById(ShoppingCart shoppingCart);

    /**
     * @param shoppingCart
     * @return
     */
    List<ShoppingCart> listShoppingCart(ShoppingCart shoppingCart);


    /**
     * @param shoppingCart
     */
    @Insert("insert into shopping_cart (id,name,image,user_id,dish_id,setmeal_id,dish_flavor,number,amount,create_time)" +
            "values (#{id},#{name},#{image},#{userId},#{dishId},#{setmealId},#{dishFlavor},#{number},#{amount},#{createTime})")
    void insertOne(ShoppingCart shoppingCart);

    /**
     * @param userId
     */
    @Delete("delete from shopping_cart where user_id=#{userId}")
    void deleteByUserId(Long userId);



}
