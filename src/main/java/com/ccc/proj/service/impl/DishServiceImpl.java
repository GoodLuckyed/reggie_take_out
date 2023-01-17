package com.ccc.proj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ccc.proj.common.CustomException;
import com.ccc.proj.dto.DishDto;
import com.ccc.proj.entity.Dish;
import com.ccc.proj.entity.DishFlavor;
import com.ccc.proj.entity.Setmeal;
import com.ccc.proj.entity.SetmealDish;
import com.ccc.proj.mapper.DishMapper;
import com.ccc.proj.service.DishFlavorService;
import com.ccc.proj.service.DishService;
import com.ccc.proj.service.SetmealDishService;
import com.ccc.proj.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 新增菜品，同时保存对应口味数据
     *
     * @param dishDto
     */
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表dish
        this.save(dishDto);
        //获取菜品id
        Long dishId = dishDto.getId();
        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        //保存菜品口味数据到菜品口味表dish_flavors
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id查询菜品基本信息和对应口味信息
     *
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();

        BeanUtils.copyProperties(dish, dishDto);
        //查询口味信息
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId, id);
        List<DishFlavor> list = dishFlavorService.list(wrapper);
        dishDto.setFlavors(list);

        return dishDto;
    }

    /**
     * 修改菜品
     *
     * @param dishDto
     */
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //修改菜品基本信息
        this.updateById(dishDto);

        //删除菜品口味
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(wrapper);

        //重新添加菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 对菜品批量或者是单个 进行停售或者是起售
     *
     * @param status
     * @param ids
     */
    @Override
    public void status(int status, List<Long> ids) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids != null, Dish::getId, ids);
        //根据数据进行批量查询
        List<Dish> list = this.list(queryWrapper);
        list = list.stream().map((item) -> {
            item.setStatus(status);
            return item;
        }).collect(Collectors.toList());
        this.updateBatchById(list);

        //菜品停售,菜品关联的套餐不能起售
        if (status == 0) {
            LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(ids != null, SetmealDish::getDishId, ids);
            List<SetmealDish> setmealDishList = setmealDishService.list(wrapper);
            List<Setmeal> setmealList = new ArrayList<>();
            setmealList = setmealDishList.stream().map((item) -> {
                Long setmealId = item.getSetmealId();
                Setmeal setmeal = setmealService.getById(setmealId);
                setmeal.setStatus(0);
                return setmeal;
            }).collect(Collectors.toList());
            setmealService.updateBatchById(setmealList);
        }
    }

    /**
     * 菜品批量删除和单个删除
     * 1.判断要删除的菜品在不在售卖的套餐中，如果在那不能删除
     * 2.要先判断要删除的菜品是否在售卖，如果在售卖也不能删除
     *
     * @param ids
     * @return
     */
    @Override
    public void deleteWithFlover(List<Long> ids) {
        //判断要删除的菜品是否关联套餐
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids != null, SetmealDish::getDishId, ids);
        List<SetmealDish> setmealDishList = setmealDishService.list(queryWrapper);
        if (setmealDishList.size() == 0) {
            //如果菜品没有关联套餐，在判断菜品是否在售卖
            LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(ids != null, Dish::getId, ids);
            //通过id查询菜品基本信息
            List<Dish> dishList = this.list(wrapper);
            for (Dish dish : dishList) {
                Integer status = dish.getStatus();
                if (status == 0) {
                    //删除菜品
                    this.removeById(dish);
                    //同时删除菜品对应的口味
                    LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                    lambdaQueryWrapper.in(ids != null, DishFlavor::getDishId, ids);
                    dishFlavorService.remove(lambdaQueryWrapper);
                } else {
                    throw new CustomException("菜品正在售卖,无法删除");
                }
            }
        } else {
            throw new CustomException("菜品关联了套餐,无法删除");
        }
    }
}
