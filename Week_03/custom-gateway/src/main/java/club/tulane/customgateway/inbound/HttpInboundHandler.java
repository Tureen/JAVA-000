package club.tulane.customgateway.inbound;

import club.tulane.customgateway.Context;
import club.tulane.customgateway.outbound.HttpOutboundHandler;
import club.tulane.customgateway.outbound.MyHttpOutboundHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpInboundHandler extends ChannelInboundHandlerAdapter {

    private static Logger logger = LoggerFactory.getLogger(HttpInboundHandler.class);
    private final String proxyServer;
    private HttpOutboundHandler handler;

    public HttpInboundHandler(String proxyServer) {
        this.proxyServer = proxyServer;
        this.handler = new MyHttpOutboundHandler(this.proxyServer);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            FullHttpRequest fullHttpRequest = (FullHttpRequest) msg;

            // 执行过滤器
            Context.getPipeline().filter(fullHttpRequest);

            handler.handle(fullHttpRequest, ctx);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }
}
