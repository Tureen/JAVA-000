package com.tulane.hatch.javacourse.week5.work2;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class Student implements Serializable {
    
    private int id;
    private String name;

}
