package club.tulane.rpcfx.client;

public class RpcfxFactory {

    private final static Rpcfx rpcfx = new RpcfxCglib();

    private RpcfxFactory() {
    }

    public static Rpcfx getInstance(){
        return rpcfx;
    }
}
