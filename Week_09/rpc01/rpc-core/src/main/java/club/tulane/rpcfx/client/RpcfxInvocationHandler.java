package club.tulane.rpcfx.client;

import club.tulane.rpcfx.api.RpcRequest;
import club.tulane.rpcfx.api.RpcResponse;
import club.tulane.rpcfx.exception.RpcfxException;
import com.alibaba.fastjson.JSON;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

class RpcfxInvocationHandler implements InvocationHandler, MethodInterceptor {

    public static final MediaType JSONTYPE = MediaType.get("application/json; charset=utf-8");

    private final Class<?> serviceClass;
    private final String url;

    public <T> RpcfxInvocationHandler(Class<T> serviceClass, String url) {
        this.serviceClass = serviceClass;
        this.url = url;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] params) throws Throwable {
        return process(method, params);
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        return process(method, objects);
    }

    private Object process(Method method, Object[] params) throws IOException {
        RpcRequest request =  new RpcRequest();
        request.setServiceClass(this.serviceClass.getName());
        request.setMethod(method.getName());
        request.setParams(params);

        RpcResponse response = post(request, url);

        if(!response.isStatus()){
            throw new RpcfxException(response.getException().getCause());
        }

        return JSON.parse(response.getResult().toString());
    }

    private RpcResponse post(RpcRequest req, String url) throws IOException {
        final String reqJson = JSON.toJSONString(req);
        System.out.println("request json: " + reqJson);

        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(JSONTYPE, reqJson))
                .build();
        final String respJson = client.newCall(request).execute().body().string();
        System.out.println("response json: " + respJson);
        return JSON.parseObject(respJson, RpcResponse.class);
    }
}
