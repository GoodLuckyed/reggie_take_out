package com.ccc.proj.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ccc.proj.entity.Employee;
import com.ccc.proj.mapper.EmployeeMapper;
import com.ccc.proj.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
