package com.imnoob.transport.netty.client;

import com.imnoob.transport.netty.cache.CallResultCache;
import com.imnoob.transport.netty.enums.ResponseCode;
import com.imnoob.transport.netty.model.RpcRequest;
import com.imnoob.transport.netty.model.RpcResponse;
import com.imnoob.transport.netty.serializer.CommonSerializer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse response) throws Exception {
        if(response.getStatusCode().equals(ResponseCode.HEART_BEAT.getCode())) return;
        CompletableFuture<RpcResponse> future = CallResultCache.get(response.getRequestId());
        future.complete(response);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                logger.info("发送心跳包 [{}]", ctx.channel().remoteAddress());
                RpcRequest request = new RpcRequest();
                request.setRequestId(UUID.randomUUID().toString().replace("-",""));
                ctx.writeAndFlush(request);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }


}
