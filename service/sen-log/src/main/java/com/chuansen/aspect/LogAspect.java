package com.chuansen.aspect;


import com.chuansen.entity.Log;
import com.chuansen.service.LogService;
import com.chuansen.utils.RequestHolder;
import com.chuansen.utils.SecurityUtils;
import com.chuansen.utils.StringUtils;
import com.chuansen.utils.ThrowableUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class LogAspect {

    @Autowired
    private LogService logService;

    private long currentTime = 0L;

    /**
     * 配置切入点
     */
    @Pointcut("@annotation(com.chuansen.aop.log.Log)")
    public void logPointcut() {
        // 该方法无方法体,主要为了让同类中其他方法使用此切入点
    }

    /**
     * 配置环绕通知
     */
    @Around("logPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object object=null;
        currentTime = System.currentTimeMillis();
        object=joinPoint.proceed();
        Log log=new Log("INFO",System.currentTimeMillis() - currentTime);
        logService.save(getUsername(), StringUtils.getIP(RequestHolder.getHttpServletRequest()),joinPoint,log);
        return object;
    }

    /**
     * 配置异常通知
     */
    @AfterThrowing(pointcut = "logPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        Log log = new Log("ERROR",System.currentTimeMillis() - currentTime);
        log.setExceptionDetail(ThrowableUtil.getStackTrace(e).getBytes());
        logService.save(getUsername(), StringUtils.getIP(RequestHolder.getHttpServletRequest()), (ProceedingJoinPoint)joinPoint, log);
    }

    public String getUsername() {
        try {
            return SecurityUtils.getUsername();
        }catch (Exception e){
            return "";
        }
    }

}
