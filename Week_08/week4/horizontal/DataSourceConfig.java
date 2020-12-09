package club.tulane.shardinghor.horizontal;

import org.apache.shardingsphere.driver.api.ShardingSphereDataSourceFactory;
import org.apache.shardingsphere.infra.config.algorithm.ShardingSphereAlgorithmConfiguration;
import org.apache.shardingsphere.sharding.api.config.ShardingRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.rule.ShardingTableRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.strategy.sharding.StandardShardingStrategyConfiguration;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

//@Configuration
public class DataSourceConfig {

    private static final String url_common = "jdbc:mysql://127.0.0.1:3306/tulane";
    private static final String url_master = "jdbc:mysql://127.0.0.1:3316/tulane";

    @Bean(name = "dataSource")
    public DataSource getDataSource() throws SQLException, IOException {
        return buildByCode();
    }

    private DataSource buildByCode() throws SQLException {
        Map<String, DataSource> dataSourceMap = new HashMap<>();
        dataSourceMap.put("ds0", DataSourceUtil.createDataSource(url_common, "123456"));
        dataSourceMap.put("ds1", DataSourceUtil.createDataSource(url_master, "123"));

        // 规则
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        // 规则算法配置
        buildAlgorithm(shardingRuleConfig);
        // 规则表配置
        buildTable(shardingRuleConfig);

        return ShardingSphereDataSourceFactory.createDataSource(dataSourceMap, Collections.singleton(shardingRuleConfig), new Properties());
    }

    private void buildAlgorithm(ShardingRuleConfiguration shardingRuleConfiguration) {
        // 配置算法: 分库
        Properties dbShardingAlgorithmrProps = new Properties();
        dbShardingAlgorithmrProps.setProperty("algorithm-expression", "ds${user_id % 2}");
        shardingRuleConfiguration.getShardingAlgorithms().put("dbShardingAlgorithm",
                new ShardingSphereAlgorithmConfiguration("INLINE", dbShardingAlgorithmrProps));

        // 配置算法: 分表
        Properties tableShardingAlgorithmrProps = new Properties();
        tableShardingAlgorithmrProps.setProperty("algorithm-expression", "t_order_info${id % 16}");
        shardingRuleConfiguration.getShardingAlgorithms().put("tableShardingAlgorithm",
                new ShardingSphereAlgorithmConfiguration("INLINE", tableShardingAlgorithmrProps));
    }

    private void buildTable(ShardingRuleConfiguration shardingRuleConfiguration) {
        // order 表配置
        ShardingTableRuleConfiguration orderTableRuleConfig = new ShardingTableRuleConfiguration("t_order_info", "ds${0..1}.t_order_info${0..15}");

        // order 表配置: 分库策略
        orderTableRuleConfig.setDatabaseShardingStrategy(new StandardShardingStrategyConfiguration("user_id", "dbShardingAlgorithm"));
        // order 表配置: 分表策略
        orderTableRuleConfig.setTableShardingStrategy(new StandardShardingStrategyConfiguration("id", "tableShardingAlgorithm"));

        // 加入规则
        shardingRuleConfiguration.getTables().add(orderTableRuleConfig);
    }


}
