package club.tulane.v2;

import club.tulane.v1.DataSourceConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DataSourceContextHolder {

    public static final String default_datasource = DataSourceConfig.MASTER;

    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

    /**
     * 切换数据源名
     * @param dbType
     */
    public static void setDB(String dbType) {
        log.debug("切换到{}数据源", dbType);
        contextHolder.set(dbType);
    }

    /**
     * 获取数据源名
     */
    public static String getDB() {
        return contextHolder.get();
    }

    /**
     * 清除数据源名
     */
    public static void clearDB() {
        contextHolder.remove();
    }
}
