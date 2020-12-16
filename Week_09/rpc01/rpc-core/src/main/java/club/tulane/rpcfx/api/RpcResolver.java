package club.tulane.rpcfx.api;

/**
 * 寻址接口
 */
public interface RpcResolver {

    Object resolve(String serviceClass);
}
