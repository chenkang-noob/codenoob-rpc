package com.imnoob.transport.netty.client;


import com.imnoob.transport.netty.cache.CallResultCache;
import com.imnoob.transport.netty.enums.CustomizeException;
import com.imnoob.transport.netty.enums.SerializeType;
import com.imnoob.transport.netty.exception.CommonException;
import com.imnoob.transport.netty.model.RpcRequest;
import com.imnoob.transport.netty.model.RpcResponse;
import com.imnoob.transport.netty.serializer.CommonSerializer;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class NettyClient {

    //TODO 建立连接  发送消息

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);



    private CommonSerializer serializer;


    public NettyClient(CommonSerializer serializer) {
        this.serializer = serializer;
    }

    public NettyClient() {
        serializer = CommonSerializer.getSerializer(SerializeType.DEFALUT_SERIALIZER.getCode());
    }



    CompletableFuture<RpcResponse> sendMsg(String serviceName, RpcRequest request) {
        CompletableFuture<RpcResponse> res = new CompletableFuture<>();
        Channel channel = ChannelProvider.getChannel(serviceName, serializer);
        if (!channel.isActive()){
            logger.error("通道未激活");
            throw new CommonException(CustomizeException.SEND_MSG_ERROR);
        }

        ChannelFuture channelFuture = channel.writeAndFlush(request).addListener((ChannelFutureListener)future -> {
            if (future.isSuccess()){
                logger.info("消息发送成功:"+request);
            }else{
                future.channel().close();
                res.completeExceptionally(future.cause());
                throw new CommonException(CustomizeException.SEND_MSG_ERROR);
            }
        });
        CallResultCache.getResultMap().put(request.getRequestId(), res);
        return res;
    }


}
