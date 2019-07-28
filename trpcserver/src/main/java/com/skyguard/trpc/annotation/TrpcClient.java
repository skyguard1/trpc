package com.skyguard.trpc.annotation;

import com.skyguard.trpc.loadbalance.IRule;
import com.skyguard.trpc.loadbalance.RandomRule;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TrpcClient {

    Class<?> tCLass() default Object.class;

    String name() default "";

    Class<? extends IRule> rule() default RandomRule.class;





}
