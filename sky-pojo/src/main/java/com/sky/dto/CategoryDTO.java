package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CategoryDTO implements Serializable {
    private Integer id;
    private String name;
    private Integer type;
    private Integer sort;
}
