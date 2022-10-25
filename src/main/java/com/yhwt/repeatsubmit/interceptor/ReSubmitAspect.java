package com.yhwt.repeatsubmit.interceptor;

import com.yhwt.repeatsubmit.annotation.ReSubmit;
import com.yhwt.repeatsubmit.util.RedisLockHelper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * ReSubmit 切面处理
 *
 * @author Administrator
 */
@Aspect
@Slf4j
public class ReSubmitAspect {

    public ReSubmitAspect(RedisLockHelper redisLockHelper, CacheKeyGenerator cacheKeyGenerator) {
        this.redisLockHelper = redisLockHelper;
        this.cacheKeyGenerator = cacheKeyGenerator;
    }

    private final RedisLockHelper redisLockHelper;
    private final CacheKeyGenerator cacheKeyGenerator;


    @Around("execution(public * *(..)) && @annotation(com.yhwt.repeatsubmit.annotation.ReSubmit)")
    public Object interceptor(ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        ReSubmit reSubmit = method.getAnnotation(ReSubmit.class);
        final String lockKey = cacheKeyGenerator.getLockKey(pjp);
        boolean getLockResult;
        long beginGetLockTime = System.currentTimeMillis();
        int retryPeriod = reSubmit.retryPeriod();
        do {
            getLockResult = redisLockHelper.lock(lockKey, reSubmit.expire(), reSubmit.timeUnit());
            log.debug("线程：{}，尝试获取锁：{}，获取结果：{}",Thread.currentThread().getName(),lockKey,getLockResult);
            //如果获取锁失败，并且还在重试时间范围内，则不停的尝试获取该锁
        } while (!getLockResult && System.currentTimeMillis() - beginGetLockTime < retryPeriod * 1000);
        if (!getLockResult) {
            log.info("分布式锁获取失败，lockKey：{}", lockKey);
            throw new RuntimeException(reSubmit.message());
        }
        try {
            return pjp.proceed();
        }catch (Throwable throwable) {
            log.error("系统运行异常", throwable);
            throw new RuntimeException("系统异常",throwable);
        } finally {
            if (reSubmit.autoRelease()) {
                redisLockHelper.unlock(lockKey);
            }
        }
    }
}