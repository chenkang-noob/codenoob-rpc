package com.imnoob.transport.netty.server;

import com.google.common.util.concurrent.RateLimiter;
import com.imnoob.transport.netty.Utils.PackageScanUtil;
import com.imnoob.transport.netty.annotation.RateLimit;
import com.imnoob.transport.netty.annotation.Service;
import com.imnoob.transport.netty.annotation.ServiceScan;
import com.imnoob.transport.netty.cache.ServiceCache;
import com.imnoob.transport.netty.codec.CommonDecoder;
import com.imnoob.transport.netty.codec.CommonEncoder;
import com.imnoob.transport.netty.enums.CustomizeException;
import com.imnoob.transport.netty.exception.CommonException;
import com.imnoob.transport.netty.handler.RateCGProxy;
import com.imnoob.transport.netty.handler.RateProxy;
import com.imnoob.transport.netty.provider.impl.NacosProvider;
import com.imnoob.transport.netty.serializer.CommonSerializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;


public class NettyServer {

    //TODO 启动服务  暴露接口
    private Integer port;
    private String serviceName;
    private String host;

    private NacosProvider nacosProvider;
    private CommonSerializer serializer;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public NettyServer(String serviceName, String host,Integer port, NacosProvider nacosProvider,CommonSerializer serializer) {
        this.port = port;
        this.serviceName = serviceName;
        this.host = host;
        this.nacosProvider = nacosProvider;
        this.serializer =serializer;
    }

    public NettyServer() {
        this.port=9000;
        this.serviceName = "rpc-provider";
        this.host = "127.0.0.1";
        nacosProvider = new NacosProvider("127.0.0.1",8848);
        this.serializer =CommonSerializer.getSerializer( CommonSerializer.PROTOBUF_SERIALIZER);
    }


    //TODO 异常处理
    public void run(){
        try {
            serviceScan();
        } catch (Exception e) {
            logger.error("注解扫描错误");
            throw new CommonException(CustomizeException.ANNOTATION_SCAN_ERROR);
        }


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
                        pipeline.addLast("encoder",new CommonEncoder(serializer));
                        pipeline.addLast(new IdleStateHandler(60, 0, 0, TimeUnit.SECONDS));
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
                       nacosProvider.registerService(serviceName,host,port);
                   }else {
                       logger.error("服务启动失败！");
                       //           删除服务
                       nacosProvider.deregisterInstance(serviceName,host,port);
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

    public void serviceScan() throws ClassNotFoundException, IOException, IllegalAccessException, InstantiationException {
        //TODO 扫描注解
        StackTraceElement[] stacks = new Throwable().getStackTrace();

        String startClass = stacks[stacks.length-1].getClassName();
        Class<?> clazz = Class.forName(startClass);
        ServiceScan annotation = clazz.getAnnotation(ServiceScan.class);
        if (annotation == null){
            logger.error("启动类未标识注解 ServiceScan");
            throw new CommonException(CustomizeException.RUN_ANNOTATION_ERROR);

        }
        String basePackageName = annotation.value();
        if ("".equals(basePackageName)){
            basePackageName = startClass.substring(0, startClass.lastIndexOf("."));
        }


        Set<Class<?>> classes = PackageScanUtil.getClasses(basePackageName);

        Iterator<Class<?>> iterator = classes.iterator();
        while (iterator.hasNext()){
            Class<?> next = iterator.next();
            Service anno = next.getAnnotation(Service.class);
            RateLimit rateAnno = next.getAnnotation(RateLimit.class);
            if (anno != null) {
                if ("".equals(anno.value())){
                    Class<?>[] interfaces = next.getInterfaces();
                    Object o = null;
                    if (rateAnno != null){
                        //TODO 限流代理
                        double limitNum = rateAnno.value();
                        int time = rateAnno.timeout();
                        TimeUnit timeUnit = rateAnno.timeUnit();
                        RateLimiter rateLimiter = RateLimiter.create(limitNum, time, timeUnit);
                        try {
                            if (interfaces.length != 0)
                                o = new RateProxy(next.newInstance(), rateLimiter).getProxy(next.getInterfaces()[0]);
                            else //没有接口的情况下使用 cglib策略
                                o = new RateCGProxy(rateLimiter).getProxy(next);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }else{
                         o = next.newInstance();
                    }

                    for (Class<?> tmp : interfaces) {
                        ServiceCache.addService(tmp.getTypeName(),o);
                    }

                }else{
                    ServiceCache.addService(anno.value(), next.newInstance());
                }

                //TODO 限流代理
//                if (rateAnno != null && anno != null){
//                    double limitNum = rateAnno.value();
//                    int time = rateAnno.timeout();
//                    TimeUnit timeUnit = rateAnno.timeUnit();
//                    RateLimiter rateLimiter = RateLimiter.create(limitNum, time, timeUnit);
//                    RateLimitCache.addRateLimit(next.getName(),rateLimiter);
//                }
            }
        }


    }





}
