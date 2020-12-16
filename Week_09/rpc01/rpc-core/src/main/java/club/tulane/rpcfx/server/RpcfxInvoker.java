package club.tulane.rpcfx.server;

import club.tulane.rpcfx.api.RpcRequest;
import club.tulane.rpcfx.api.RpcResolver;
import club.tulane.rpcfx.api.RpcResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class RpcfxInvoker {

    private RpcResolver resolver;

    public RpcfxInvoker(RpcResolver resolver) {
        this.resolver = resolver;
    }

    public RpcResponse invoke(RpcRequest request){
        RpcResponse response = new RpcResponse();
        final String serviceClass = request.getServiceClass();

        Object service = resolver.resolve(serviceClass);
        Method method = resolveMethodFromClass(service.getClass(), request.getMethod());
        try {
            final Object result = method.invoke(service, request.getParams());

            response.setResult(JSON.toJSONString(result, SerializerFeature.WriteClassName));
            response.setStatus(true);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            response.setException(e);
            response.setStatus(false);
        }
        return response;
    }

    private Method resolveMethodFromClass(Class<?> klass, String methodName) {
        return Arrays.stream(klass.getMethods()).filter(m -> methodName.equals(m.getName())).findFirst().get();
    }

}
