package com.imnoob.transport.netty.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imnoob.transport.netty.cache.ServiceCache;
import com.imnoob.transport.netty.enums.ResponseCode;
import com.imnoob.transport.netty.model.RpcRequest;
import com.imnoob.transport.netty.model.RpcResponse;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.xml.ws.Response;
import java.lang.reflect.Method;

public class ServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                logger.info("长时间未收到心跳包，断开连接...");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("处理过程调用时有错误发生:");
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest request) throws Exception {
        logger.info("接收到客户端请求:"+request.getRequestId());
        if (request.getInterfaceName() == null){
            RpcResponse rpcResponse = new RpcResponse();
            rpcResponse.setRequestId(request.getRequestId());
            rpcResponse.setStatusCode(ResponseCode.HEART_BEAT.getCode());
            rpcResponse.setMessage(ResponseCode.HEART_BEAT.getMsg());
            channelHandlerContext.writeAndFlush(rpcResponse);
        }
        else if (channelHandlerContext.channel().isActive() && channelHandlerContext.channel().isWritable()) {
            RpcResponse rpcResponse = requestHandler(request);
            channelHandlerContext.writeAndFlush(rpcResponse);
        } else {
            logger.error("通道不可写");
        }

    }

    private RpcResponse requestHandler(RpcRequest request){
        Object res;
        String key = request.getInterfaceName();
        Object service = ServiceCache.findService(key);
        if (service != null){
            try {
                Method method = service.getClass().getMethod(request.getMethodName(), request.getParamTypes());
                Object[] objs = request.getParameters();
                Class<?>[] types = request.getParamTypes();
                //TODO 解决LinkedHashMap的情况
                for (int i=0;i < objs.length;i++) {
                    ObjectMapper mapper = new ObjectMapper();
                    Object o = mapper.convertValue(objs[i], types[i]);
                    objs[i] = o;
                }
                res = method.invoke(service, request.getParameters());
                return RpcResponse.OK(request.getRequestId(),res);
            } catch (Exception e) {
                logger.error("方法调用失败");
                return RpcResponse.ERROR(request.getRequestId(), ResponseCode.METHOM_CALL_ERROR);
            }
        }else {
            return RpcResponse.ERROR(request.getRequestId(),ResponseCode.SERVICE_NOT_FOUND);
        }
    }
}
