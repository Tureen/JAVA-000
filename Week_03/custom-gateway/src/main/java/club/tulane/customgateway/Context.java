package club.tulane.customgateway;

import club.tulane.customgateway.filter.FilterPipeline;

public class Context {

    private static FilterPipeline filterPipeline;

    private Context() {
    }

    static {
        filterPipeline = new FilterPipeline();
    }

    public static FilterPipeline getPipeline(){
        return filterPipeline;
    }
}
