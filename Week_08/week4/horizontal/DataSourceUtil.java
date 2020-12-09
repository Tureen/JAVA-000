package club.tulane.shardinghor.horizontal;

import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DataSourceUtil {

    private static final String USER_NAME = "root";

    public static DataSource createDataSource(final String dataSourceUrl, final String passwd) {
        HikariDataSource result = new HikariDataSource();
        result.setDriverClassName("com.mysql.jdbc.Driver");
        result.setJdbcUrl(String.format("%s?serverTimezone=UTC&useSSL=false&useUnicode=true&characterEncoding=UTF-8", dataSourceUrl));
        result.setUsername(USER_NAME);
        result.setPassword(passwd);
        return result;
    }
}
