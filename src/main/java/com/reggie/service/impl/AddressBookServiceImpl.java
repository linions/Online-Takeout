package com.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.mapper.AddressBookMapper;
import com.reggie.mapper.UserMapper;
import com.reggie.pojo.AddressBook;
import com.reggie.pojo.User;
import com.reggie.service.AddressBookService;
import com.reggie.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook>  implements AddressBookService {

}
