package com.sky.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "每页显示员工数据量")
public class EmployeePageQueryDTO implements Serializable {
    private String name;
    private int page;
    private int pageSize;
}
