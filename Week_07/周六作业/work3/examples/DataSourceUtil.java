package club.tulane.shardingdatabase.examples;

import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DataSourceUtil {

    private static final String USER_NAME = "root";

    private static final String PASSWORD = "123";


    public static DataSource createDataSource(final String dataSourceUrl) {
        HikariDataSource result = new HikariDataSource();
        result.setDriverClassName("com.mysql.jdbc.Driver");
        result.setJdbcUrl(String.format("%s?serverTimezone=UTC&useSSL=false&useUnicode=true&characterEncoding=UTF-8", dataSourceUrl));
        result.setUsername(USER_NAME);
        result.setPassword(PASSWORD);
        return result;
    }
}
