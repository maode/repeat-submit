package com.yhwt.repeatsubmit.interceptor;

import com.yhwt.repeatsubmit.annotation.ReSubmit;
import com.yhwt.repeatsubmit.annotation.ReSubmitParam;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * redis key 生成器实现;
 * @author Administrator
 */
public class LockKeyGenerator implements CacheKeyGenerator {

    /**
     * 锁key生成器<br/>
     * 将 ReSubmit 注解的 prefix 值 和 被 ReSubmitParam 注解标记的参数值进行拼接，作为锁的 key 返回。<br/>
     * 如果 ReSubmit 注解的 prefix 值为空，则使用被打点的方法的全限定类名和方法名作为前缀。
     * @param pjp PJP
     * @return
     */
    @Override
    public String getLockKey(ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        ReSubmit lockAnnotation = method.getAnnotation(ReSubmit.class);
        final Object[] args = pjp.getArgs();
        final Parameter[] parameters = method.getParameters();
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isEmpty(lockAnnotation.prefix())) {
            builder.append(method.getDeclaringClass().getName())
                    .append(lockAnnotation.delimiter())
                    .append(method.getName());
        } else {
            builder.append(lockAnnotation.prefix());
        }
        // 默认解析方法里面带 ReSubmitParam 注解的属性,如果没有尝试着解析实体对象中的
        for (int i = 0; i < parameters.length; i++) {
            final ReSubmitParam annotation = parameters[i].getAnnotation(ReSubmitParam.class);
            if (annotation == null) {
                continue;
            }else if(isNotBaseType(parameters[i].getType().getName())){
                throw new RuntimeException("请将 ReSubmitParam 注解添加在基本类型，或对象的基本类型字段上");
            }
            builder.append(lockAnnotation.delimiter()).append(args[i]);
        }
        final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            final Object object = args[i];
            final Field[] fields = object.getClass().getDeclaredFields();
            for (Field field : fields) {
                final ReSubmitParam annotation = field.getAnnotation(ReSubmitParam.class);
                if (annotation == null) {
                    continue;
                }else if(isNotBaseType(field.getType().getName())){
                    throw new RuntimeException("请将 ReSubmitParam 注解添加在基本类型，或对象的基本类型字段上");
                }
                field.setAccessible(true);
                builder.append(lockAnnotation.delimiter()).append(ReflectionUtils.getField(field, object));
            }
        }
        return builder.toString();
    }


    /**
     * 判断是否为基本类型
     *
     * @param typeName
     * @return 基本类型返回 false，非基本类型返回true
     */
    private boolean isNotBaseType(String typeName) {
        if ("java.lang.Integer".equals(typeName) ||
                "java.lang.Byte".equals(typeName) ||
                "java.lang.Long".equals(typeName) ||
                "java.lang.Double".equals(typeName) ||
                "java.lang.Float".equals(typeName) ||
                "java.lang.Character".equals(typeName) ||
                "java.lang.Short".equals(typeName) ||
                "java.lang.Boolean".equals(typeName) ||
                "java.lang.String".equals(typeName)
        ) {
            return false;
        } else {
            return true;
        }
    }
}