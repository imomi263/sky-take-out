package com.sky.dto;


import com.sky.entity.SetmealDish;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetmealDTO implements Serializable {

    private Long id;
    private String name;
    private String image;
    private String description;
    private BigDecimal price;
    private Integer status;
    private Long categoryId;
    // 当前套餐包含的多种菜品
    private List<SetmealDish> setmealDishes = new ArrayList<>();


}
