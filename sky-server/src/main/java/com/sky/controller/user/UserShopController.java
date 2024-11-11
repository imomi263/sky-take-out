package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/shop")
@Tag(name="用户商店接口")
public class UserShopController {

    @Resource
    private RedisTemplate redisTemplate;

    private String SHOP_STATUS="SHOP_STATUS";


    @GetMapping("/status")
    @Operation(summary = "获取店铺状态")
    public Result<Integer> getStatus(){
        Integer status= (Integer) redisTemplate.opsForValue().get(SHOP_STATUS);

        return Result.success(Integer.valueOf(status));

    }
}
