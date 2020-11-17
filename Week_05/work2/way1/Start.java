package com.tulane.hatch.javacourse.week5.work2.way1;

import com.tulane.hatch.javacourse.week5.work2.Student;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class Start {

    public static void main(String[] args) {
        // 加载配置文件
        ApplicationContext applicationContext = new FileSystemXmlApplicationContext(
                "src/main/java/com/tulane/hatch/javacourse/week5/work2/way1/applicationContext.xml");

        // 搜索Bean
        final Student student = (Student) applicationContext.getBean("student123");
        System.out.println(student.getId() + "-" + student.getName());
    }
}
