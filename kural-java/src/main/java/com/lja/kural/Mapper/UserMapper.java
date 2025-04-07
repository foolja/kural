package com.lja.kural.Mapper;

import com.lja.kural.Bean.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper
{
    public User findByUsername(String username);
    public void addUser(User user);
    public String selectUserId(User user);
}
