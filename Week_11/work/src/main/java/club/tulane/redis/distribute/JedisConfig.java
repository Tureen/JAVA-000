package club.tulane.redis.distribute;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * jedis连接配置
 * Created by Tulane
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "data.redis")
public class JedisConfig {

    private String host;
    private String password;
    private String port;
    private String timeout;
    private String maxTotal;
    private String maxIdle;
    private String minIdle;
}
