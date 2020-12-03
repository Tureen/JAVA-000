package club.tulane.v3;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class SlaveRoute {

    private AtomicInteger atomicInteger = new AtomicInteger(0);

    @Resource
    @Qualifier(SlaveDataSourcesConfig.DATASOURCE_COLLECTION)
    private List<SlaveDataSourcesConfig.DataSourceObj> dataSources;

    public String route(){
        final int i = atomicInteger.getAndIncrement();
        final SlaveDataSourcesConfig.DataSourceObj dataSourceObj = dataSources.get(i % dataSources.size());
        return dataSourceObj.getName();
    }
}
