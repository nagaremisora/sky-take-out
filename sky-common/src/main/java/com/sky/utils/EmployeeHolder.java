package com.sky.utils;

import com.sky.vo.EmployeeLoginVO;

public class EmployeeHolder {
    private static final ThreadLocal<EmployeeLoginVO> tl = new ThreadLocal<>();

    public static EmployeeLoginVO getUser(){
        return tl.get();
    }

    public static void saveUser(EmployeeLoginVO user){
        tl.set(user);
    }

    public static void removeUser(){
        tl.remove();
    }
}
