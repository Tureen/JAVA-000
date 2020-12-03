package club.tulane.v1;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    public static final String MASTER = "master";
    public static final String SLAVE = "slave";

    private static final String url_master = "jdbc:mysql://127.0.0.1:3316/db";
    private static final String url_slave = "jdbc:mysql://127.0.0.1:3317/db";
    private static final String user = "root";
    private static final String passwd = "123";

    @Bean(name = MASTER)
    public DataSource dataSource(){
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url_master);
        config.setUsername(user);
        config.setPassword(passwd);
        config.setAutoCommit(false);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", 300);
        return new HikariDataSource(config);
    }

    @Bean(name = SLAVE)
    public DataSource dataSourceSlave(){
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url_slave);
        config.setUsername(user);
        config.setPassword(passwd);
        config.setAutoCommit(false);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", 300);
        return new HikariDataSource(config);
    }
}
