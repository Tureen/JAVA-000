package club.tulane.v2;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Order(-1)
@Component
public class DynamicDataSourceAspect {

    @Before("@annotation(RealOnly)")
    public void beforeSwitchDataSource(JoinPoint joinPoint){
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        RealOnly realOnly = method.getDeclaredAnnotation(RealOnly.class);
        String dataSource = realOnly.value();
        DataSourceContextHolder.setDB(dataSource);
    }

    @After("@annotation(RealOnly)")
    public void afterSwitchDataSource(){
        DataSourceContextHolder.clearDB();
    }
}
