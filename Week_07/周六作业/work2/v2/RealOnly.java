package club.tulane.v2;

import club.tulane.v1.DataSourceConfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RealOnly {

    String value() default DataSourceConfig.SLAVE;
}
