package club.tulane.customgateway.outbound;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

public interface HttpOutboundHandler {

    void handle(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx);
}
