package club.tulane;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by Tulane
 * 2019/3/12
 */
@Slf4j
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@ComponentScan("club.tulane.v3")
public class Application implements ApplicationContextAware {

    public static ApplicationContext ctx;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        log.info("service start success");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Application.ctx = applicationContext;
    }
}
