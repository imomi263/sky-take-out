package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("select * from user where openid=#{openid}")
    User getByOpenId(String openid);

    @Select("selete * from user where id=#{id}")
    User getById(Long id);


    void insertUser(User user);
}
