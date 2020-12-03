package club.tulane.v3;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 动态数据源配置
 */
@Configuration
public class DynamicDataSourceConfig {

    public static final String DYNAMIC_DATA_SOURCE = "dynamicDataSource";

    @Resource
    @Qualifier(DataSourceConfig.MASTER)
    private DataSource masterDB;

    @Resource
    @Qualifier(SlaveDataSourcesConfig.DATASOURCE_COLLECTION)
    private List<SlaveDataSourcesConfig.DataSourceObj> dataSources;

    @Bean(name = DYNAMIC_DATA_SOURCE)
    public DataSource dynamicDataSource() {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        //设置默认数据源, 当 determineCurrentLookupKey() 方法获取的为空, 会使用此默认
        dynamicDataSource.setDefaultTargetDataSource(masterDB);

        Map<Object, Object> dsMap = new HashMap<>();
        dsMap.put(DataSourceConfig.MASTER, masterDB);
        // 将 slave 数据源集合注入
        dataSources.forEach(obj -> dsMap.put(obj.getName(), obj.getDataSource()));
        dynamicDataSource.setTargetDataSources(dsMap);
        return dynamicDataSource;
    }
}
