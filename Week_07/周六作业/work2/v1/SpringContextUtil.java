package club.tulane.v1;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 首先, 使用 Component 注解, 让此类被扫描到, 成为一个Bean
 * 其次, 实现 ApplicationContextAware, 使Spring启动时, 检测到此实现接口的类, 并注入上下文
 *
 * 将上下引用到, 使用静态变量, 可以在其他任意地方得到上下文
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {

    public static ApplicationContext ctx;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.ctx = applicationContext;
    }
}
