package com.tulane.hatch.javacourse.week5.work1;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class Start {

    public static void main(String[] args) {
        ISchool school = new School();
        InvocationHandler myHandler = new MyHandler<>(school);
        final ISchool proxyInstance = (ISchool) Proxy.newProxyInstance(school.getClass().getClassLoader(), new Class[]{ISchool.class}, myHandler);
        proxyInstance.ding();
    }
}
