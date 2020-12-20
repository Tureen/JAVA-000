package club.tulane.rpcfx.netty;

import club.tulane.rpcfx.api.RpcResponse;
import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

/**
 * 客户端 handler
 */
@Slf4j
public class ClientHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

    // 用于同步等待响应返回
    private CountDownLatch countDownLatch;

    private RpcResponse response;

    public ClientHandler() {
        initCountDownLatch();
    }

    private void initCountDownLatch() {
        countDownLatch = new CountDownLatch(1);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) {
        try {
            ByteBuf buf = msg.content();
            String result = buf.toString(CharsetUtil.UTF_8);
            response = JSON.parseObject(result, RpcResponse.class);
        } finally {
            countDownLatch.countDown();
        }
    }

    /**
     * 获取 channelRead0 中的响应结果
     * @return
     * @throws InterruptedException
     */
    public RpcResponse getResponse() throws InterruptedException {
        countDownLatch.await();
        return response;
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.debug("ClientHandler被移除");
        super.handlerRemoved(ctx);
    }
}
