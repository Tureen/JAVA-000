package com.tulane.hatch.javacourse.week5.work1;

/**
 * 接口实现
 */
public class School implements ISchool {
    
    @Override
    public void ding(){
    
        System.out.println("Class1 have " + this.getClass().getName());
        
    }
    
}
