package com.imnoob.transport.netty.client;

import com.imnoob.transport.netty.cache.CallResultCache;
import com.imnoob.transport.netty.model.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class ClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse response) throws Exception {
        logger.info("客户端读取消息: "+response.getRequestId());
        logger.info(response.toString());
        CompletableFuture<RpcResponse> future = CallResultCache.get(response.getRequestId());
        future.complete(response);
    }
}
