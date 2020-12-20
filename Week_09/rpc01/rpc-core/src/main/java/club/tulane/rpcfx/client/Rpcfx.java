package club.tulane.rpcfx.client;

/**
 * 代理接口定义
 */
public interface Rpcfx {

    <T> T create(final Class<T> serviceClass, final String url);
}
