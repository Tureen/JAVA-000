package com.tulane.hatch.javacourse.week5.work3;

import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@EnableAutoConfiguration
public class App implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);

        final ApplicationContext applicationContext = App.applicationContext;
        final Student student = (Student) applicationContext.getBean("student");
        final Klass klass = (Klass) applicationContext.getBean("klass");
        final School school = (School) applicationContext.getBean("school");
        System.out.println(student.toString());
        System.out.println(klass.toString());
        System.out.println(school.toString());
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        App.applicationContext = applicationContext;
    }
}
