package org.got.web.gotweb.common.Aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.got.web.gotweb.common.annotations.ToLowerCase;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LowerCaseAspect {

    @Around("@annotation(toLowerCase)")
    public Object processLowerCase(ProceedingJoinPoint joinPoint, ToLowerCase toLowerCase) throws Throwable {
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof String) {
                args[i] = ((String) args[i]).toLowerCase();
            }
        }
        return joinPoint.proceed(args);
    }
}