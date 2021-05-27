package com.imnoob.transport.netty.client;

import com.imnoob.transport.netty.codec.CommonDecoder;
import com.imnoob.transport.netty.codec.CommonEncoder;
import com.imnoob.transport.netty.model.RpcRequest;
import com.imnoob.transport.netty.serializer.JsonSerializer;
import com.imnoob.transport.netty.serializer.KryoSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;
import java.util.UUID;

public class NettyClient {

    //TODO 建立连接  发送消息

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);


    private static final String HOST = "127.0.0.1";
    private static final Integer PORT = 9000;

    static {

    }

    public void run() throws Exception{
        EventLoopGroup group = new NioEventLoopGroup();

        try {


            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            //得到pipeline
                            ChannelPipeline pipeline = ch.pipeline();
                            //加入相关handler
                            pipeline.addLast("decoder", new CommonDecoder());
                            pipeline.addLast("encoder", new CommonEncoder(new KryoSerializer()));
                            //加入自定义的handler
                            pipeline.addLast(new ClientHandler());
                        }
                    });

            ChannelFuture channelFuture = bootstrap.connect(HOST, PORT).sync();
            //得到channel
            Channel channel = channelFuture.channel();

            channel.closeFuture().addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    System.out.println("通道关闭");
                }
            });
            //客户端需要输入信息，创建一个扫描器
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                String msg = scanner.nextLine();
                //通过channel 发送到服务器端
                RpcRequest request = new RpcRequest();
                request.setRequestId(UUID.randomUUID().toString().replace("-","")+msg);

                channel.writeAndFlush(request);
            }
        }finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception{
        new NettyClient().run();
    }
}
