package club.tulane.rpcfx.client;

import club.tulane.rpcfx.api.RpcRequest;
import club.tulane.rpcfx.api.RpcResponse;
import club.tulane.rpcfx.exception.RpcfxException;
import club.tulane.rpcfx.netty.NettyHttpClient;
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
import java.net.URISyntaxException;

/**
 * 客户端代理类执行器
 */
class RpcfxInvocationHandler implements InvocationHandler, MethodInterceptor {

    private final Class<?> serviceClass;
    private final String url;

    public <T> RpcfxInvocationHandler(Class<T> serviceClass, String url) {
        this.serviceClass = serviceClass;
        this.url = url;
    }

    /**
     * InvocationHandler JDK动态代理
     * @param proxy
     * @param method
     * @param params
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] params) throws Throwable {
        return process(method, params);
    }

    /**
     * MethodInterceptor 字节码代理
     * @param o
     * @param method
     * @param objects
     * @param methodProxy
     * @return
     * @throws Throwable
     */
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        return process(method, objects);
    }

    /**
     * 代理具体实现: 远程Http调用
     * @param method
     * @param params
     * @return
     * @throws URISyntaxException
     * @throws InterruptedException
     * @throws IOException
     */
    private Object process(Method method, Object[] params) throws URISyntaxException, InterruptedException, IOException {
        RpcRequest request =  new RpcRequest();
        request.setServiceClass(this.serviceClass.getName());
        request.setMethod(method.getName());
        request.setParams(params);

        RpcResponse response = postOnNetty(request, url);

        if(!response.isStatus()){
            throw new RpcfxException(response.getException().getCause());
        }

        return JSON.parse(response.getResult().toString());
    }

    /**
     * Http请求: OkHttpClient实现
     * @param req
     * @param url
     * @return
     * @throws IOException
     */
    private RpcResponse postOnOkHttpClient(RpcRequest req, String url) throws IOException {
        final String reqJson = JSON.toJSONString(req);
        System.out.println("request json: " + reqJson);

        OkHttpClient client = new OkHttpClient();
        final MediaType jsonType = MediaType.get("application/json; charset=utf-8");
        final Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(jsonType, reqJson))
                .build();
        final String respJson = client.newCall(request).execute().body().string();
        System.out.println("response json: " + respJson);
        final RpcResponse rpcResponse = JSON.parseObject(respJson, RpcResponse.class);
        return rpcResponse;
    }

    /**
     * Http 请求: Netty实现
     * @param req
     * @param url
     * @return
     * @throws URISyntaxException
     * @throws InterruptedException
     */
    private RpcResponse postOnNetty(RpcRequest req, String url) throws URISyntaxException, InterruptedException {
        final String reqJson = JSON.toJSONString(req);
        System.out.println("request json: " + reqJson);

        final NettyHttpClient instance = NettyHttpClient.getInstance();
        final RpcResponse response = instance.getResponse(req, url);
        return response;
    }
}
