package club.tulane.v3;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 可动态调整最终返回的 DataSource 的数据源
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    /**
     * 返回的数据源 bean name, 为{@link DataSourceContextHolder}中的数据源名
     * @return
     */
    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceContextHolder.getDB();
    }
}
