package com.tulane.hatch.javacourse.week5.work2.way3;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.stereotype.Component;

@Component
@AutoConfigureAfter(name = "bean")
@Data
public class Beans {

    /**
     * 注入
     */
    @Autowired
    private Bean bean;
}
