package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

//这个注解表明JwtProperties类是一个Spring组件，
// Spring容器会负责创建这个类的实例，并将其管理为一个Bean。
// 这意味着你可以在应用程序的其他部分通过依赖注入来使用这个类。
@Component
@Data
//这个注解来自Spring Boot，用于将配置文件中的属性绑定到一个Java对象上。
// prefix="login-reg.jwt"
//参数指定了配置文件中属性的前缀，
// 这意味着所有以login-reg.jwt开头的配置属性都会被绑定到JwtProperties类的相应字段上。
@ConfigurationProperties(prefix="login-reg.jwt")
public class JwtProperties {

    // 员工jwt令牌配置
    private String employeeSecretKey;
    private long employTtl;
    private String employeeTokenName;

    // 用户jwt令牌配置
    private String userSecretKey;
    private String userTokenName;
    private long userTtl;


}
