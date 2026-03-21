package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Tag(name = "菜品管理接口")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 新蹭菜品
     * @param dishDTO
     * @return
     */
    @PostMapping
    @Operation(summary = "添加菜品")
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("添加菜品：{}", dishDTO);
        dishService.saveWithFlavor(dishDTO);

        //清理缓存数据
        redisTemplate.delete("dish_" + dishDTO.getCategoryId());
        return Result.success();

    }

    /**
     * 菜品查询
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @Operation(summary = "菜品查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品查询:{}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 批量删除
      * 必须加上这个注解，才能让ids参数被封装成List<Long>
     * @param ids
     * @return
     */
    @DeleteMapping
    @Operation(summary = "删除菜品")
    public Result delete(@RequestParam List<Long> ids){
        log.info("删除菜品：{}", ids);
        dishService.deleteBatch(ids);

        //清理所有的缓存数据
        Set<String> keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);
        return Result.success();
    }

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("根据id查询菜品：{}", id);
        DishVO dishVO = dishService.getByIdwithFlavor(id);
        return Result.success(dishVO);
    }

    /**
     * 修改菜品
     * @param dishDTO
     * @return
     */
    @PutMapping
    @Operation(summary = "修改菜品")
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品：{}", dishDTO);
        dishService.updateWithFlavor(dishDTO);

        //清理缓存数据
        Set<String> keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);
        return Result.success();
    }
}
