package com.yhwt.repeatsubmit;

import com.yhwt.repeatsubmit.interceptor.CacheKeyGenerator;
import com.yhwt.repeatsubmit.interceptor.LockKeyGenerator;
import com.yhwt.repeatsubmit.interceptor.ReSubmitAspect;
import com.yhwt.repeatsubmit.util.RedisLockHelper;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 防重复提交自动配置类
 * @author ZhengMaoDe
 */
@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class RepeatSubmitAutoConfigration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(StringRedisTemplate.class)
    public RedisLockHelper getRedisLockHelper(StringRedisTemplate stringRedisTemplate) {
        return new RedisLockHelper(stringRedisTemplate);
    }
    @Bean
    @ConditionalOnMissingBean
    public CacheKeyGenerator getCacheKeyGenerator(){
        return new LockKeyGenerator();
    }
    @Bean
    @ConditionalOnMissingBean
    public ReSubmitAspect getLockMethodInterceptor(RedisLockHelper redisLockHelper, CacheKeyGenerator cacheKeyGenerator){
        return new ReSubmitAspect(redisLockHelper,cacheKeyGenerator);
    }

}
