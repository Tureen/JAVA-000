package club.tulane.v3;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * 动态数据源配置
 */
@Configuration
public class SlaveDataSourcesConfig {

    public static final String DATASOURCE_COLLECTION = "dataSourceMap";

    @Resource
    @Qualifier(DataSourceConfig.SLAVE)
    private DataSource slaveDB;

    @Resource
    @Qualifier(DataSourceConfig.SLAVE_2)
    private DataSource slaveDB2;

    @Resource
    @Qualifier(DataSourceConfig.SLAVE_3)
    private DataSource slaveDB3;

    @Bean(name = DATASOURCE_COLLECTION)
    public List<DataSourceObj> dataSourceMap() {
        List<DataSourceObj> dataSourceObjs = new ArrayList<>();
        dataSourceObjs.add(new DataSourceObj(DataSourceConfig.SLAVE, slaveDB));
        dataSourceObjs.add(new DataSourceObj(DataSourceConfig.SLAVE_2, slaveDB2));
        dataSourceObjs.add(new DataSourceObj(DataSourceConfig.SLAVE_3, slaveDB3));
        return dataSourceObjs;
    }

    @Getter
    @AllArgsConstructor
    class DataSourceObj{
        private String name;
        private DataSource dataSource;
    }
}
