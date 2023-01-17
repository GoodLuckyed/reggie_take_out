package com.ccc.proj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ccc.proj.entity.ShoppingCart;
import com.ccc.proj.mapper.ShoppingCartMapper;
import com.ccc.proj.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
