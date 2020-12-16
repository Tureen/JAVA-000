package club.tulane.rpcfx.client;

import com.alibaba.fastjson.parser.ParserConfig;
import net.sf.cglib.proxy.Enhancer;

public class RpcfxCglib implements Rpcfx{

    static{
        ParserConfig.getGlobalInstance().addAccept("club.tulane");
    }

    public <T> T create(final Class<T> serviceClass, final String url){
        Enhancer en = new Enhancer();
        en.setSuperclass(serviceClass);
        en.setCallback(new RpcfxInvocationHandler(serviceClass, url));
        return (T) en.create();
    }
}
