package club.tulane.rpcfx.client;

import com.alibaba.fastjson.parser.ParserConfig;

import java.lang.reflect.Proxy;

public class RpcfxJDK implements Rpcfx{

    static {
        ParserConfig.getGlobalInstance().addAccept("club.tulane");
    }

    public <T> T create(final Class<T> serviceClass, final String url){
        return (T) Proxy.newProxyInstance(RpcfxJDK.class.getClassLoader(), new Class[]{serviceClass},
                new RpcfxInvocationHandler(serviceClass, url));
    }

}
