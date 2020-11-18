package com.tulane.hatch.javacourse.week5.work3;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

@Data
public class School{

    @Autowired
    private Student student;

}
