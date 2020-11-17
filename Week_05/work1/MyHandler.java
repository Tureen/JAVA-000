package com.tulane.hatch.javacourse.week5.work1;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 动态代理
 * @param <T>
 */
public class MyHandler<T> implements InvocationHandler {

    private T target;

    public MyHandler(T target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final Aop aop = new Aop();
        aop.start();
        // 反射调用传入对象的指定方法
        final Object result = method.invoke(target, args);
        aop.end();
        return result;
    }
}
