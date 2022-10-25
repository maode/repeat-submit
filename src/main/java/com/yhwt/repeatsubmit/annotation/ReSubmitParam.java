package com.yhwt.repeatsubmit.annotation;

import java.lang.annotation.*;


/**
 * 控制防重复提交的参数，需配合 @ReSubmit 一起使用<br/>
 * 想要防止哪个基本类型的参数重复提交，则在该参数上添加该注解。<br/>
 * 如果参数为实体类对象，则可以在该对象的防止重复的关键属性上添加该注解。
 * @see ReSubmit
 * @author Administrator
 */
@Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ReSubmitParam {

    /**
     * 字段名称
     *
     * @return String
     */
    String name() default "";
}
