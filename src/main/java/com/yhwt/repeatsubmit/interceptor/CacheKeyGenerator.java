package com.yhwt.repeatsubmit.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * redis key生成器接口
 * @author Administrator
 */
public interface CacheKeyGenerator {

    /**
     * 获取AOP参数,生成指定缓存Key
     *
     * @param pjp PJP
     * @return 缓存KEY
     */
    String getLockKey(ProceedingJoinPoint pjp);
}
