package com.tulane.hatch.javacourse.week5.work2.way2;

import com.tulane.hatch.javacourse.week5.work2.School;
import com.tulane.hatch.javacourse.week5.work2.Student;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * 注解注入Bean
 */
@Configuration
public class Beans {

    private final Student student = new Student(2, "小明");

    @Bean(name = "student")
    public Student student(){
        return student;
    }

    @Bean(name = "school")
    @ConditionalOnBean(name = "student")
    public School school(){
        return new School(Arrays.asList(student));
    }
}
