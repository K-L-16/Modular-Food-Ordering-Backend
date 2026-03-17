package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private WeChatProperties wxProperties;

    @Autowired
    private UserMapper userMapper;

    public static final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";
    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        //调用微信接口服务，获得道歉微信用户的openid
        String openid = getOpenid(userLoginDTO.getCode());
        //判断openid是否为空，如果为空表示登录失败，抛出业务异常
        if (openid == null) {
            log.error("登录失败，openid为空");
            throw new RuntimeException("登录失败");
        }
        //根据openid查询用户是否已经存在，如果已经存在，则无需处理，直接返回用户信息
        User user = userMapper.getByOpenid(openid);
        //如果信用户，自动完成注册
        if (user == null){
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }

        //返回用户对象
        return user;
    }

    /**
     * 调用微信接口服务，获得当前微信用户的openid
     * @param code
     * @return
     */
    private String getOpenid(String code) {
        //定义参数
        Map<String, String> map = new HashMap<>();
        map.put("appid", wxProperties.getAppid());
        map.put("secret", wxProperties.getSecret());
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");

        String json = HttpClientUtil.doGet(WX_LOGIN_URL, map);
        JSONObject jsonObject = JSON.parseObject(json);
        return jsonObject.getString("openid");
    }
}
