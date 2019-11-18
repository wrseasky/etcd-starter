package com.seasky.starter.etcd.web.service;

import com.seasky.starter.etcd.web.entity.User;
import com.seasky.starter.etcd.web.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    @Autowired
    UserMapper userMapper;

    public User getUserByUserName(String userName){
        return userMapper.getUserByUserName(userName);
    }

    public int insertUser(User user){
        return userMapper.insertUser(user);
    }

}
