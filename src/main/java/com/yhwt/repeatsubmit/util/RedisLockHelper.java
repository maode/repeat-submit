package com.yhwt.repeatsubmit.util;


import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * redis 分布式锁实现
 * @author Administrator
 */
public class RedisLockHelper {


    /**
     * 如果要求比较高可以通过注入的方式分配
     */
    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newScheduledThreadPool(0);

    private final StringRedisTemplate stringRedisTemplate;

    public RedisLockHelper(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 获取锁
     *
     * @param lockKey  lockKey
     * @param timeout  超时时间
     * @param timeUnit 过期单位
     * @return 成功返回 true，失败返回 false
     */
    public boolean lock(String lockKey, long timeout, final TimeUnit timeUnit) {
        boolean tryLock = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, "k",timeout,timeUnit);
        return tryLock;
    }


    /**
     * @see <a href="http://redis.io/commands/set">Redis Documentation: SET</a>
     */
    public void unlock(String lockKey) {
        unlock(lockKey, 0, TimeUnit.MILLISECONDS);
    }

    /**
     * 延迟unlock
     *
     * @param lockKey   key
     * @param delayTime 延迟时间
     * @param unit      时间单位
     */
    public void unlock(final String lockKey, long delayTime, TimeUnit unit) {
        if (StringUtils.isEmpty(lockKey)) {
            return;
        }
        if (delayTime <= 0) {
            doUnlock(lockKey);
        } else {
            EXECUTOR_SERVICE.schedule(() -> doUnlock(lockKey), delayTime, unit);
        }
    }

    /**
     * @param lockKey key
     */
    private void doUnlock(final String lockKey) {
        stringRedisTemplate.delete(lockKey);
    }

}
