package club.tulane.rpcfx.client;

public interface Rpcfx {

    <T> T create(final Class<T> serviceClass, final String url);
}
