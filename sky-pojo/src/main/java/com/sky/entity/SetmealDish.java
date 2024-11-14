package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetmealDish implements Serializable {

    private Long id;
    private String name;
    private BigDecimal price;
    private Integer copies;
    private Long setmealId;
    private Long dishId;
}

