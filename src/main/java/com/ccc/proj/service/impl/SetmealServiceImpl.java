package com.ccc.proj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ccc.proj.common.CustomException;
import com.ccc.proj.dto.SetmealDto;
import com.ccc.proj.entity.Dish;
import com.ccc.proj.entity.Setmeal;
import com.ccc.proj.entity.SetmealDish;
import com.ccc.proj.mapper.SetmealMapper;
import com.ccc.proj.service.DishService;
import com.ccc.proj.service.SetmealDishService;
import com.ccc.proj.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    @Lazy
    private DishService dishService;

    /**
     * 新增套餐，同时添加套餐包含的菜品
     * @param setmealDto
     */
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息
        this.save(setmealDto);
        //添加套餐包含的菜品
        //获取套餐的id
        Long id = setmealDto.getId();
        List<SetmealDish> list = setmealDto.getSetmealDishes();
        list =  list.stream().map((item) -> {
            item.setSetmealId(id);
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(list);
    }

    /**
     * 根据id查询对应的套餐数据
     * @param id
     */
    @Override
    public SetmealDto getWithSetmealDish(Long id) {
        SetmealDto setmealDto = new SetmealDto();

        //查询套餐的基本信息
        Setmeal setmeal = this.getById(id);
        BeanUtils.copyProperties(setmeal,setmealDto);
        //查询套餐关联的菜品
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(id != null,SetmealDish::getSetmealId,id);
        List<SetmealDish> list = setmealDishService.list(queryWrapper);

        setmealDto.setSetmealDishes(list);
        return setmealDto;
    }

    /**
     * 修改套餐，同时修改套餐关联的菜品
     * @param setmealDto
     */
    @Override
    public void updateWithSetmealDish(SetmealDto setmealDto) {
        //修改套餐基本信息
        this.updateById(setmealDto);
        //删除套餐关联的菜品
        Long id = setmealDto.getId();
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(id != null,SetmealDish::getSetmealId,id);
        setmealDishService.remove(queryWrapper);
        //重新添加套餐修改后的菜品
        List<SetmealDish> list = setmealDto.getSetmealDishes();
        list = list.stream().map((item) ->{
            item.setSetmealId(id);
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(list);
    }

    /**
     * 对套餐批量或单个，进行停售或起售
     * @param status
     * @param ids
     */
    @Override
    public void status(int status, List<Long> ids) {
        //通过前端传来的id查询套餐信息
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids != null,Setmeal::getId,ids);
        List<Setmeal> list = this.list(queryWrapper);
        //遍历查询的套餐集合
        list = list.stream().map((item) -> {
            item.setStatus(status);
            return item;
        }).collect(Collectors.toList());
        //修改状态
        this.updateBatchById(list);

        //套餐起售，套餐关联的菜品不能停售
        if(status == 1) {
            LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(ids != null, SetmealDish::getSetmealId, ids);
            List<SetmealDish> setmealDishList = setmealDishService.list(wrapper);
            List<Dish> dishList = new ArrayList<>();
            dishList = setmealDishList.stream().map((item) -> {
                Long dishId = item.getDishId();
                Dish dish = dishService.getById(dishId);
                dish.setStatus(1);
                return dish;
            }).collect(Collectors.toList());
            dishService.updateBatchById(dishList);
        }
    }

    /**
     * 对套餐批量或单个，进行删除
     * @param ids
     */
    @Override
    public void deleteWithSetmealDish(List<Long> ids) {
        //查询套餐基本信息
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids != null,Setmeal::getId,ids);
        List<Setmeal> list = this.list(queryWrapper);
        //判断套餐是否正在售卖，如果正在售卖则不能删除
        for (Setmeal setmeal : list) {
            Integer status = setmeal.getStatus();
            if(status == 0){
                this.removeById(setmeal);
                //同时删除套餐关联的菜品
                LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
                wrapper.in(ids != null,SetmealDish::getSetmealId,ids);
                setmealDishService.remove(wrapper);
            }else {
                throw new CustomException("套餐正在售卖，无法删除");
            }
        }
    }
}
