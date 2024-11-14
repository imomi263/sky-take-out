package com.sky.service.Impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private static final String WX_LOGIN="https://api.weixin.qq.com/sns/jscode2session";

    @Resource
    private WeChatProperties WeChatProperties;

    @Resource
    private UserMapper userMapper;

    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        // 调用微信接口服务获得微信用户的openId

        String response=getOpenId(userLoginDTO.getCode());
        JSONObject jsonObject=JSON.parseObject(response);
        String openid=jsonObject.getString("openid");

        // 判断openId是否真的获取到
        if(openid==null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        // 当前用户是否是对我们系统的新的用户--> 存入我们的User表
        User user=userMapper.getByOpenId(openid);
        if(user==null){
            user=User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insertUser(user);
        }

        // 返回用户对象

        return user;
    }

    private String getOpenId(String code){
        Map<String,String> map=new HashMap<String,String>();
        map.put("appid",WeChatProperties.getAppId());
        map.put("secret",WeChatProperties.getSecret());
        map.put("grant_type","authorization_code");
        map.put("js_code",code);


        return HttpClientUtil.doGet(WX_LOGIN,map);
    }
}
