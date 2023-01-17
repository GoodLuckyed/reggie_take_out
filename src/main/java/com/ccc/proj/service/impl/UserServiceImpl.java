package com.ccc.proj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ccc.proj.entity.User;
import com.ccc.proj.mapper.UserMapper;
import com.ccc.proj.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
