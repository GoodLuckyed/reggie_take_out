package com.ccc.proj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ccc.proj.dto.SetmealDto;
import com.ccc.proj.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    /**
     * 新增套餐，同时添加套餐包含的菜品
     * @param setmealDto
     */
    void saveWithDish(SetmealDto setmealDto);

    /**
     * 根据id查询对应的套餐数据
     * @param id
     */
    SetmealDto getWithSetmealDish(Long id);

    /**
     * 修改套餐，同时修改套餐关联的菜品
     * @param setmealDto
     */
    void updateWithSetmealDish(SetmealDto setmealDto);

    /**
     * 对套餐批量或单个，进行停售或起售
     * @param status
     * @param ids
     */
    void status(int status, List<Long> ids);

    /**
     * 对套餐批量或单个，进行删除
     * @param ids
     */
    void  deleteWithSetmealDish(List<Long> ids);
}
