package com.luffy.EcommerceBackend.aop;

import com.luffy.EcommerceBackend.service.UserService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;



@Component
@Aspect
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);


    @Before("execution(* com.luffy.EcommerceBackend.service.UserService.getAllUsers(..)) || execution(* com.luffy.EcommerceBackend.service.UserService.validateLogin(..))" )
    public void logMethodCall(JoinPoint jp){
        logger.info("Method Called " + jp.getSignature().getName());
    }

    @After("execution(* com.luffy.EcommerceBackend.service.UserService.getAllUsers(..)) || execution(* com.luffy.EcommerceBackend.service.UserService.validateLogin(..))" )
    public void logMethodExecuted(JoinPoint jp){
        logger.info("Method Executed " + jp.getSignature().getName());
    }
    @AfterThrowing("execution(* com.luffy.EcommerceBackend.service.UserService.getAllUsers(..)) || execution(* com.luffy.EcommerceBackend.service.UserService.validateLogin(..))" )
    public void logMethodCrash(JoinPoint jp){
        logger.info("Method crashed " + jp.getSignature().getName());
    }
}
