package com.ccc.proj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ccc.proj.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
