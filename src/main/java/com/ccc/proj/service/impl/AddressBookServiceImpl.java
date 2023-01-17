package com.ccc.proj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ccc.proj.entity.AddressBook;
import com.ccc.proj.mapper.AddressBookMapper;
import com.ccc.proj.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
