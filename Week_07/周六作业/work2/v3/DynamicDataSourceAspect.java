package club.tulane.v3;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Order(-1)
@Component
public class DynamicDataSourceAspect {

    @Autowired
    private SlaveRoute slaveRoute;

    @Before("@annotation(RealOnly)")
    public void beforeSwitchDataSource(){
        final String dataSource = slaveRoute.route();
        DataSourceContextHolder.setDB(dataSource);
    }

    @After("@annotation(RealOnly)")
    public void afterSwitchDataSource(){
        DataSourceContextHolder.clearDB();
    }
}
