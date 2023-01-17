package com.ccc.proj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ccc.proj.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
