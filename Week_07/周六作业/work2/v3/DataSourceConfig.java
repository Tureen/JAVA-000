package club.tulane.v3;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    public static final String MASTER = "master";
    public static final String SLAVE = "slave";
    public static final String SLAVE_2 = "slave2";
    public static final String SLAVE_3 = "slave3";

    private static final String url_master = "jdbc:mysql://127.0.0.1:3316/db";
    private static final String url_slave = "jdbc:mysql://127.0.0.1:3317/db";
    private static final String url_slave_2 = "jdbc:mysql://127.0.0.1:3318/db";
    private static final String url_slave_3 = "jdbc:mysql://127.0.0.1:3319/db";
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

    @Bean(name = SLAVE_2)
    public DataSource dataSourceSlave2(){
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url_slave_2);
        config.setUsername(user);
        config.setPassword(passwd);
        config.setAutoCommit(false);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", 300);
        return new HikariDataSource(config);
    }

    @Bean(name = SLAVE_3)
    public DataSource dataSourceSlave3(){
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url_slave_3);
        config.setUsername(user);
        config.setPassword(passwd);
        config.setAutoCommit(false);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", 300);
        return new HikariDataSource(config);
    }
}
