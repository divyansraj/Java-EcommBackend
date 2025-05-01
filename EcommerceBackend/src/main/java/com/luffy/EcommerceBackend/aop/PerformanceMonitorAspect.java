package com.luffy.EcommerceBackend.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class PerformanceMonitorAspect {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceMonitorAspect.class);

    @Around("execution(* com.luffy.EcommerceBackend.service.UserService.getAllUsers(..)) || execution(* com.luffy.EcommerceBackend.service.UserService.validateLogin(..))" )
    public Object calculateTime(ProceedingJoinPoint jp) throws Throwable {
        long start = System.currentTimeMillis();
        Object obj = jp.proceed();
        long end = System.currentTimeMillis();
        logger.info("Time taken by method {} is {}ms", jp.getSignature().getName(), end - start);
        return obj;
    }
}
