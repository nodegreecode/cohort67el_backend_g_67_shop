package de.ait.g_67_shop.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class AspectLogging {

    private final Logger logger = LoggerFactory.getLogger(AspectLogging.class);

    @Pointcut(value = "execution(* de.ait.g_67_shop.service.*.*(..))")
    public void anyMethodsInService() {

    }

    @Before("anyMethodsInService()")
    public void beforeAnyServiceMethod(JoinPoint joinPoint) {
        String className = getClassName(joinPoint);
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        logger.debug("Method {} of the class {} called with arguments: {}", methodName, className, Arrays.toString(args));
    }

    @After("anyMethodsInService()")
    public void afterAnyServiceMethod(JoinPoint joinPoint) {
        String className = getClassName(joinPoint);
        String methodName = joinPoint.getSignature().getName();
        logger.debug("Method {} of the class {} finished its work", methodName, className);
    }

    @AfterReturning(pointcut = "anyMethodsInService()", returning = "result")
    public void afterReturningAnyServiceMethod(JoinPoint joinPoint, Object result) {
        String className = getClassName(joinPoint);
        String methodName = joinPoint.getSignature().getName();
        logger.debug("Method {} of the class {} returned result: {}", methodName, className, result);
    }

    @AfterThrowing(pointcut = "anyMethodsInService()", throwing = "e")
    public void afterThrowingAnyServiceMethod(JoinPoint joinPoint, Exception e) {
        String className = getClassName(joinPoint);
        String methodName = joinPoint.getSignature().getName();
        logger.warn("Method {} of the class {} threw an exception", methodName, className, e);
    }

    private String getClassName(JoinPoint joinPoint) {
        return AopUtils.getTargetClass(joinPoint.getTarget()).getSimpleName();
    }

}
