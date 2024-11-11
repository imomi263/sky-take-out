package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/shop")
@Tag(name="店铺接口")
public class ShopController {

    @Resource
    private RedisTemplate redisTemplate;

    private String SHOP_STATUS="SHOP_STATUS";
    @PutMapping("/{status}")
    @Operation(summary = "设置店铺状态")
    public Result setStatus(@PathVariable Integer status){

        redisTemplate.opsForValue().set(SHOP_STATUS, status);
        return Result.success();
    }

    @GetMapping("/status")
    @Operation(summary = "获取店铺状态")
    public Result<Integer> getStatus(){
        Integer status= (Integer) redisTemplate.opsForValue().get(SHOP_STATUS);

        return Result.success(Integer.valueOf(status));
        //return Result.success(Integer.valueOf(status));

    }
}
