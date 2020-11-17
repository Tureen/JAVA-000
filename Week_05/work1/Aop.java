package com.tulane.hatch.javacourse.week5.work1;

import java.util.Date;

/**
 * AOP类
 */
public class Aop {

    private Date nowDate;

    /**
     * 方法开始执行
     */
    public void start(){
        nowDate = new Date();
    }

    /**
     * 方法结束执行
     */
    public void end(){
        System.out.println("耗时:" + (new Date().getTime() - nowDate.getTime()) + "ms");
    }
}
