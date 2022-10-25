package com.yhwt.repeatsubmit.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 防重复提交注解，需配合 @ReSubmitParam 一起使用
 * @see ReSubmitParam
 * @author Administrator
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ReSubmit {

    /**
     * redis分布式锁key的前缀，如果为空，则默认使用当前全限定类名+方法名作为key前缀
     *
     * @return redis 锁key的前缀
     */
    String prefix() default "";

    /**
     * 锁的超时自动失效时间,默认为5秒
     *
     * @return 锁的超时自动失效时间
     */
    int expire() default 5;

    /**
     * 超时自动失效的时间单位（默认：秒）
     *
     * @return
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * <p>Key的分隔符（默认 :）</p>
     * <p>生成的Key：N:SO1008:500</p>
     *
     * @return String
     */
    String delimiter() default ":";

    /**
     * 请求执行完，是否自动释放锁<br/>
     * true 请求执行完，自动释放锁<br/>
     * false 请求执行完，不自动释放，等待锁过期后释放
     * @return boolean
     */
    boolean autoRelease() default true;

    /**
     * 拦截后的提示消息
     * @return String
     */
    String message() default "请求过于频繁，请稍后再试";

    /**
     * 竞争锁失败时，尝试重新竞争的时间,（单位：秒）<br/>
     * 合理设置该值，该值应该小于熔断时间、ribbon超时时间，否则会引发前端超时后，后端逻辑仍旧执行的情况。<br/>
     * 如果该值等于0，则获取锁失败时，会直接抛出获取锁失败的异常。<br/>
     * 如果该值大于0，则会在该值限定的时间范围内不停尝试竞争该锁，如超出该时间范围，仍未竞争到锁，则抛出获取锁失败的异常。
     * @return
     */
    int retryPeriod() default 0;
}