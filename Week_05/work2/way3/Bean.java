package com.tulane.hatch.javacourse.week5.work2.way3;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.springframework.stereotype.Component;

/**
 * Component 注解, 扫描注入为Bean
 */
@Component(value = "bean")
@Data
@AllArgsConstructor
@ToString
public class Bean {

    private int id;
    private String name;

    public Bean() {
        this.id = 0;
        this.name = "小无";
    }
}
