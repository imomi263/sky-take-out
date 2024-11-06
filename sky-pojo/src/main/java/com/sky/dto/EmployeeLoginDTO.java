package com.sky.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;

import lombok.Data;


@Data
@Schema(description = "员工登录传递的数据模型")
public class EmployeeLoginDTO implements java.io.Serializable{

    @Schema(name = "用户名")
    private String username;

    @Schema(name = "密码")
    private String password;

}
