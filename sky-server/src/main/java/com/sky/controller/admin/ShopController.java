package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

//指定bean名称
@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Slf4j
@Tag(name = "店铺相关接口")
public class ShopController {

    @Autowired
    private RedisTemplate redisTemplate;

    public static final String KEY = "SHOP_STATUS";

    /**
     * 设置店铺营业状态
     * @param status
     * @return
     */
    @PutMapping("/{status}")
    @Operation(summary = "设置营业状态")
    public Result setStatus(@PathVariable Integer status) {
        log.info("设置店铺营业状态为：{}", status == 1? "营业中" : "打烊中");
        redisTemplate.opsForValue().set(KEY, status);
        return Result.success();
    }

    /**
     * 获取营业状态
     * @return
     */
    @GetMapping("/status")
    @Operation(summary = "获取营业状态")
    public Result<Integer> getStatus() {
        Integer status = (Integer) redisTemplate.opsForValue().get(KEY);
        log.info("获取营业状态为：{}", status == 1? "营业中" : "打烊中");
        return Result.success(status != null ? status : 0);
    }
}
