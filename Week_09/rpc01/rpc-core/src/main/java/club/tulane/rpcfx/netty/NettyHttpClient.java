package club.tulane.rpcfx.netty;

import club.tulane.rpcfx.api.RpcRequest;
import club.tulane.rpcfx.api.RpcResponse;
import com.alibaba.fastjson.JSON;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

public class NettyHttpClient {

    /**
     * Channel 连接池, Key为目标的 ip+端口
     */
    private ConcurrentHashMap<String, Channel> channelPool = new ConcurrentHashMap<>();

    /**
     * Netty 请求
     * @param req
     * @param url
     * @return
     * @throws URISyntaxException
     * @throws InterruptedException
     */
    public RpcResponse getResponse(RpcRequest req, String url) throws URISyntaxException, InterruptedException {
        URI uri = new URI(url);
        String cacheKey = uri.getHost() + ":" + uri.getPort();

        // 连接池中获取
        Channel channel = channelPool.get(cacheKey);
        if (channel == null) {
            channel = createConnect(uri.getHost(), uri.getPort());
            channelPool.put(cacheKey, channel);
        }

        final ClientHandler clientHandler = new ClientHandler();
        // 将原 ClientHandler 替换新的, 作用是每次请求使用新 handler 对象
        channel.pipeline().replace(ClientHandler.class, "client handler", clientHandler);
        // 转换成 Netty Http 请求
        final FullHttpRequest httpRequest = convertToHttp(req, uri);
        channel.writeAndFlush(httpRequest);

        return clientHandler.getResponse();
    }

    /**
     * RpcReuqest 转 FullHttpRequest
     * @param request
     * @param uri
     * @return
     */
    private FullHttpRequest convertToHttp(RpcRequest request, URI uri) {
        final String reqJson = JSON.toJSONString(request);
        final byte[] bytes = reqJson.getBytes(StandardCharsets.UTF_8);
        // 连接 FullHttpRequest, 这里需要用 http 1.1 协议, 如果是1.0, 建立连接发送数据后会立刻断开
        // 而 Netty PipeLine 中, 断开会触发 channelUnRegistered 事件, 将Pipeline上的handler都remove掉
        // 下次使用池中的 channel, 就会产生错误
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST,
                uri.toASCIIString(),
                Unpooled.wrappedBuffer(bytes));
        // 添加Http请求头: ip, keep_alive, content_length, content_type (json)
        httpRequest.headers().add(HttpHeaderNames.HOST, uri.getHost());
        httpRequest.headers().add(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        httpRequest.headers().add(HttpHeaderNames.CONTENT_LENGTH, httpRequest.content().readableBytes());
        httpRequest.headers().add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
        return httpRequest;
    }

    /**
     * 客户端建立 channel 通道
     * @param host
     * @param port
     * @return
     * @throws InterruptedException
     */
    private Channel createConnect(String host, int port) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.AUTO_CLOSE, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ClientHandlerInitializer());
        final ChannelFuture f = b.connect(host, port).sync();
        return f.channel();
    }

    private NettyHttpClient() {
    }

    /**
     * 单例实现: 枚举懒汉
     */
    private enum EnumSingleton {
        INSTANCE;
        private NettyHttpClient instance;

        EnumSingleton() {
            instance = new NettyHttpClient();
        }

        public NettyHttpClient getSingleton() {
            return instance;
        }
    }

    public static NettyHttpClient getInstance() {
        return EnumSingleton.INSTANCE.getSingleton();
    }

}
