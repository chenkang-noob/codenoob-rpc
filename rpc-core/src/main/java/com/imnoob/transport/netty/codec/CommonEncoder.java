package com.imnoob.transport.netty.codec;

import com.imnoob.transport.netty.constant.RpcConstant;
import com.imnoob.transport.netty.enums.PackageType;
import com.imnoob.transport.netty.model.RpcRequest;
import com.imnoob.transport.netty.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CommonEncoder extends MessageToByteEncoder {


    private CommonSerializer serializer;

    public CommonEncoder(CommonSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        //TODO 提取枚举
        //TODO 写入 魔数  包类型  序列化类型  数据长度
        byteBuf.writeInt(RpcConstant.MAGIC_NUM);  //魔数
        //包类型
        if (o instanceof RpcRequest){
            byteBuf.writeInt(PackageType.RPC_REQUEST.getCode());

        }else{
            byteBuf.writeInt(PackageType.RPC_RESPONSE.getCode());
        }
        //序列化类型
        byteBuf.writeInt(serializer.getCode());


        byte[] data = serializer.serializer(o);
        byteBuf.writeInt(data.length);   //data 长度
        byteBuf.writeBytes(data);
    }
}
