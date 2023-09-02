package com.sky.utils;

import com.sky.vo.UserLoginVO;

public class UserHolder {
    private static final ThreadLocal<UserLoginVO> thread = new ThreadLocal<>();

    public static UserLoginVO getUser(){
        return thread.get();
    }

    public static void saveUser(UserLoginVO user){
        thread.set(user);
    }

    public static void removeUser(){
        thread.remove();
    }
}
