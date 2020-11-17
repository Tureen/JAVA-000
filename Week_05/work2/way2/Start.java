package com.tulane.hatch.javacourse.week5.work2.way2;

import com.tulane.hatch.javacourse.week5.work2.School;
import com.tulane.hatch.javacourse.week5.work2.Student;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Start {

    public static void main(String[] args) {
        SpringApplication.run(Start.class, args);

        // 检测对象是否注入成Bean
        final ApplicationContext ctx = SpringContextUtil.ctx;
        final Student student = (Student) ctx.getBean("student");
        final School school = (School) ctx.getBean("school");
        System.out.println(student.toString());
        System.out.println(school.toString());
    }
}
