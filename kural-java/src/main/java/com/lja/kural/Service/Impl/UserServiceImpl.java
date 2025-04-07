package com.lja.kural.Service.Impl;

import com.lja.kural.Bean.User;
import com.lja.kural.Mapper.UserMapper;
import com.lja.kural.Service.UserService;
import com.lja.kural.Utils.IdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Override
    @Transactional
    public String addUser(User user)
    {
        user.setUserId(IdUtil.generateUserId());
        userMapper.addUser(user);
        return user.getUserId();
    }

    @Override
    @Transactional
    public String selectUserId(User user)
    {
        return userMapper.selectUserId(user);
    }
}
