package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.enumeration.OperationType;
import com.sky.utils.EmployeeHolder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
@Aspect
public class AutoFillAspect {
    @Pointcut("execution(* com.sky.service.impl.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void fillPointcut(){}

    @Before("fillPointcut()")
    public void beforeDone(JoinPoint joinPoint) {
        System.out.println("--------切面-------------");
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        OperationType operation = signature.getMethod().getAnnotation(AutoFill.class).value();
        Object[] args = joinPoint.getArgs();
        if(args.length == 0 || args == null){
            return;
        }
        Object entity = args[0];
        Long id = EmployeeHolder.getUser().getId();
        try {
            Method setCreateUser;
            if(operation == OperationType.INSERT) {
                setCreateUser = entity.getClass().getDeclaredMethod("setCreateUser", Long.class);
                setCreateUser.invoke(entity, id);
            }
            Method setUpdateUser = entity.getClass().getDeclaredMethod("setUpdateUser", Long.class);
            setUpdateUser.invoke(entity, id);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
