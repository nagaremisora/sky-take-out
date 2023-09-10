package com.sky.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.constant.MessageConstant;
import com.sky.constant.RedisConstant;
import com.sky.entity.User;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.result.Result;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import com.sky.utils.UserHolder;
import com.sky.vo.UserLoginVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private static final String LOGIN_INTERFACE_URI = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 微信登录
     * @param code
     * @return
     */
    @Override
    public Result<UserLoginVO> login(String code) {
        String openid = getOpenid(code);
        if (StrUtil.isEmpty(openid)){
            return Result.error(MessageConstant.LOGIN_FAILED);
        }

        // 第一次注册
        User user = query().eq("openid", openid).one();
        if(user == null) {
            save(User.builder().openid(openid).build());
            user = query().eq("openid", openid).one();
        }

        // 生成token
        String token = UUID.randomUUID().toString();
        String tokenKey = RedisConstant.WX_USER_TOKEN + token;
        UserLoginVO userLoginVO = UserLoginVO.builder().id(user.getId()).openid(openid).token(token).build();
        Map<String, Object> userMap = BeanUtil.beanToMap(userLoginVO, new HashMap<>(),
                CopyOptions.create().setIgnoreNullValue(true).setFieldValueEditor((fieldName, fieldValue)->fieldValue.toString()));
        redisTemplate.opsForHash().putAll(tokenKey, userMap);
        redisTemplate.expire(tokenKey, 10L, TimeUnit.MINUTES);
        UserHolder.saveUser(userLoginVO);

        return Result.success(userLoginVO);
    }

    /**
     * 获取用户唯一标识
     * @param code
     * @return
     */
    private String getOpenid(String code) {
        Map<String,String> loginMap = new HashMap<>();
        loginMap.put("appid", weChatProperties.getAppid());
        loginMap.put("secret", weChatProperties.getSecret());
        loginMap.put("js_code", code);
        loginMap.put("grant_type", "authorization_code");

        String json = HttpClientUtil.doGet(LOGIN_INTERFACE_URI, loginMap);
        JSONObject jsonObject = JSONObject.parseObject(json);
        String openid = jsonObject.getString("openid");
        return openid;
    }
}
