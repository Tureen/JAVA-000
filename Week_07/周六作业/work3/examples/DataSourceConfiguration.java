package club.tulane.shardingdatabase.examples;

import org.apache.shardingsphere.driver.api.ShardingSphereDataSourceFactory;
import org.apache.shardingsphere.replicaquery.api.config.ReplicaQueryRuleConfiguration;
import org.apache.shardingsphere.replicaquery.api.config.rule.ReplicaQueryDataSourceRuleConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@Configuration
public class DataSourceConfiguration {

    private static final String url_master = "jdbc:mysql://127.0.0.1:3316/db";
    private static final String url_slave = "jdbc:mysql://127.0.0.1:3317/db";
    private static final String url_slave_2 = "jdbc:mysql://127.0.0.1:3318/db";
    private static final String url_slave_3 = "jdbc:mysql://127.0.0.1:3319/db";

    @Bean(name = "dataSource")
    public DataSource getDataSource() throws SQLException {
        // 创建主从库数据源连接池
        Map<String, DataSource> dataSourceMap = new HashMap<>();
        dataSourceMap.put("master", DataSourceUtil.createDataSource(url_master));
        dataSourceMap.put("slave", DataSourceUtil.createDataSource(url_slave));
        dataSourceMap.put("slave2", DataSourceUtil.createDataSource(url_slave_2));
        dataSourceMap.put("slave3", DataSourceUtil.createDataSource(url_slave_3));

        // 创建主从配置信息类
        ReplicaQueryDataSourceRuleConfiguration dataSourceConfig = new ReplicaQueryDataSourceRuleConfiguration(
                "master_slave_datasource", "master",
                Arrays.asList("slave", "slave2", "slave3"), null);
        // 创建规则配置类
        ReplicaQueryRuleConfiguration ruleConfig = new ReplicaQueryRuleConfiguration(
                Collections.singleton(dataSourceConfig), Collections.emptyMap());

        // 工厂获得数据源
        return ShardingSphereDataSourceFactory.createDataSource(dataSourceMap, Collections.singleton(ruleConfig), new Properties());
    }
}
