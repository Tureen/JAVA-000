package club.tulane.customgateway.filter;

import io.netty.handler.codec.http.FullHttpRequest;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class FilterPipeline {

    private List<Filter> filters = new LinkedList<>();

    public void addLast(Filter filter){
        filters.add(filter);
    }

    public void filter(FullHttpRequest fullRequest){
        final Iterator<Filter> iterator = filters.iterator();
        while(iterator.hasNext()){
            final Filter next = iterator.next();
            next.filter(fullRequest);
        }
    }
}
