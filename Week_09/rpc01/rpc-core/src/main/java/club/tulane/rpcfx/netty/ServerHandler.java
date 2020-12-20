package club.tulane.rpcfx.netty;

import club.tulane.rpcfx.api.RpcRequest;
import club.tulane.rpcfx.api.RpcResponse;
import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.nio.charset.StandardCharsets;

/**
 * 服务层 handler
 */
public abstract class ServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) {
        ByteBuf buf = msg.content();
        String result = buf.toString(CharsetUtil.UTF_8);
        final RpcRequest rpcRequest = JSON.parseObject(result, RpcRequest.class);
        // RPC 调用, 使用抽象方法, 将实际调用延迟到实现类
        final RpcResponse response = invoke(rpcRequest);
        // 转换为 FullHttpResponse
        final FullHttpResponse httpResponse = convertToHttp(response);
        ctx.writeAndFlush(httpResponse);
    }

    /**
     * RpcResponse 转 FullHttpResponse
     * @param response
     * @return
     */
    private FullHttpResponse convertToHttp(RpcResponse response) {
        final String respJson = JSON.toJSONString(response);
        final byte[] bytes = respJson.getBytes(StandardCharsets.UTF_8);
        FullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(bytes));
        // 添加Http请求头: ip, keep_alive, content_length, content_type (json)
        httpResponse.headers().add(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        httpResponse.headers().add(HttpHeaderNames.CONTENT_LENGTH, httpResponse.content().readableBytes());
        httpResponse.headers().add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
        return httpResponse;
    }

    protected abstract RpcResponse invoke(RpcRequest rpcRequest);

}
