package com.sky.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.interceptor.JwtTokenAdminInterceptor;

import com.sky.json.JacksonObjectMapper;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import io.swagger.v3.oas.models.OpenAPI;

import java.util.List;

@Configuration
@Slf4j
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

    @Resource
    private JwtTokenAdminInterceptor jwtTokenAdminInterceptor;

    /*
        注册自定义拦截器
        @param register
     */
    protected void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册拦截器...");

        registry.addInterceptor(jwtTokenAdminInterceptor)
                    .addPathPatterns("/admin/**")
                    .excludePathPatterns("/admin/employee/login");

    }


    /*
        扩展消息转换器
        @param converters
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter converter=new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(new JacksonObjectMapper());
        converters.add(converters.size()-1,converter);
    }

    /*
            通过knife4j生成接口文档
            @return
         */
    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                // 分组名称
                .group("app-api")
                // 接口请求路径规则
                .pathsToMatch("/**")
                .build();
    }

    /*
        设置knife4j的首页
        @return
     */
    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                // 接口文档标题
                .info(new Info().title("API接口文档")

                        // 接口文档简介
                        .description("苍穹外卖接口文档")
                        // 接口文档版本
                        .version("0.0.1-SNAPSHOT")
                        // 开发者联系方式
                        .contact(new Contact().name("imomi"))
                );

    }

    /*
        设置静态资源映射
        @param registry
     */

    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }












}
