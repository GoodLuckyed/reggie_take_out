package com.ccc.proj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ccc.proj.entity.OrderDetail;
import com.ccc.proj.mapper.OrderDetailMapper;
import com.ccc.proj.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
