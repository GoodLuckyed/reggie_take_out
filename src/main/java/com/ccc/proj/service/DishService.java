package com.ccc.proj.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.ccc.proj.dto.DishDto;
import com.ccc.proj.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    /**
     * 新增菜品，同时保存对应口味数据
     * @param dishDto
     */
    void saveWithFlavor(DishDto dishDto);

    /**
     * 根据id查询菜品基本信息和对应口味信息
     * @param id
     * @return
     */
    DishDto getByIdWithFlavor(Long id);

    /**
     * 修改菜品
     * @param dishDto
     */
    void updateWithFlavor(DishDto dishDto);

    /**
     * 对菜品批量或者是单个 进行停售或者是起售
     * @param status
     * @param ids
     */
    void status(int status, List<Long> ids);

    /**
     * 菜品批量删除和单个删除
     *  1.判断要删除的菜品在不在售卖的套餐中，如果在那不能删除
     *  2.要先判断要删除的菜品是否在售卖，如果在售卖也不能删除
     * @param ids
     * @return
     */
    void deleteWithFlover(List<Long> ids);
}
