package com.ccc.proj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ccc.proj.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
