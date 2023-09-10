package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.entity.User;
import com.sky.result.Result;
import com.sky.vo.UserLoginVO;

public interface UserService extends IService<User> {
    Result<UserLoginVO> login(String code);
}
