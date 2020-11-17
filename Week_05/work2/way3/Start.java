package com.tulane.hatch.javacourse.week5.work2.way3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Start {

    public static void main(String[] args) {
        SpringApplication.run(Start.class, args);

        // 检测对象是否注入成Bean
        final ApplicationContext ctx = SpringContextUtil.ctx;
        final Bean bean = (Bean) ctx.getBean("bean");
        final Beans beans = (Beans) ctx.getBean("beans");
        System.out.println(bean.toString());
        System.out.println(beans.toString());
    }
}
