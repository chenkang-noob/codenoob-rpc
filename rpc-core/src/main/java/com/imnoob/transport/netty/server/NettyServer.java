package com.imnoob.transport.netty.server;

import com.imnoob.transport.netty.codec.CommonDecoder;
import com.imnoob.transport.netty.codec.CommonEncoder;
import com.imnoob.transport.netty.provider.NacosProvider;
import com.imnoob.transport.netty.serializer.JsonSerializer;
import com.imnoob.transport.netty.serializer.KryoSerializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyServer {

    //TODO 启动服务  暴露接口
    private Integer port;
    private String serviceName;
    private String host;
    private NacosProvider nacosProvider;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public NettyServer(Integer port, String serviceName, String host,NacosProvider nacosProvider) {
        this.port = port;
        this.serviceName = serviceName;
        this.host = host;

        this.nacosProvider = nacosProvider;
    }

    public NettyServer() {
        this.port=9000;
        this.serviceName = "rpc-provider";
        this.host = "127.0.0.1";

        nacosProvider = new NacosProvider("127.0.0.1",8848);
    }


    public void run(){
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.TCP_NODELAY,true)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast("decoder",new CommonDecoder());
                        pipeline.addLast("encoder",new CommonEncoder(new KryoSerializer()));
                        pipeline.addLast(new ServerHandler());
                        //坑： netty handler的执行顺序
                    }
                });

       try {
           ChannelFuture sync = bootstrap.bind(port).sync();
           sync.addListener(new GenericFutureListener<Future<? super Void>>() {
               @Override
               public void operationComplete(Future<? super Void> future) throws Exception {
                   if (future.isSuccess()){
                       logger.info("服务监听启动成功：" + port);
                       //           注册服务
                       nacosProvider.registerService("rpc-provider",host,port);
                   }else {
                       logger.error("服务启动失败！");
                       //           删除服务
                       nacosProvider.deregisterInstance("rpc-provider",host,port);
                   }
               }
           });



           ChannelFuture close = sync.channel().closeFuture().sync();
           close.addListener(new GenericFutureListener<Future<? super Void>>() {
               @Override
               public void operationComplete(Future<? super Void> future) throws Exception {
                   if (future.isSuccess()){
                       logger.info("连接关闭");
                   }
               }
           });
       }catch (InterruptedException e){
           logger.error("中断");
       }finally {
           bossGroup.shutdownGracefully();
           workerGroup.shutdownGracefully();
       }


    }

    public static void main(String[] args) {
        new NettyServer().run();
    }
}
