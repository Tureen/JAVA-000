package com.tulane.hatch.javacourse.week5.work3;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//@ComponentScan("com.tulane.hatch.javacourse.week5.work3")
@ConditionalOnProperty(
        prefix = "tulane",
        name = "enable",
        havingValue = "true",
        matchIfMissing = true
)
public class TulaneConfiguration {

    @Bean
    public Student student(){
        return new Student(1, "小明");
    }

    @Bean
    public Klass klass(){
        return new Klass();
    }

    /**
     * 如果要确保School加载时可以依赖注入student
     * 1. 同一个 Configuration 中把 School Bean 放在 Student Bean 之下
     * 2. 不同 Configuration 中, 用 @AutoConfigureAfter 标识顺序
     * @return
     */
    @Bean
    @ConditionalOnBean(name = "student")
    public School school(){
        return new School();
    }
}
