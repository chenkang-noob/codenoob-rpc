package com.imnoob.transport.netty.client;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.imnoob.transport.netty.cache.LoadBalanceCache;
import com.imnoob.transport.netty.codec.CommonDecoder;
import com.imnoob.transport.netty.codec.CommonEncoder;
import com.imnoob.transport.netty.enums.CustomizeException;
import com.imnoob.transport.netty.exception.CommonException;
import com.imnoob.transport.netty.loadbalance.LoadBalance;
import com.imnoob.transport.netty.loadbalance.RandomLoadBalance;
import com.imnoob.transport.netty.loadbalance.RoundLoadBalance;
import com.imnoob.transport.netty.provider.NacosProvider;
import com.imnoob.transport.netty.serializer.CommonSerializer;
import com.imnoob.transport.netty.serializer.KryoSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

public class ChannelProvider {

    //存储所有建立连接的  通道
    private static ConcurrentHashMap<String, Channel> channelMap = new ConcurrentHashMap<>();
    private static NacosProvider nacosProvider = new NacosProvider();
    private static EventLoopGroup eventLoopGroup;

    private static Bootstrap bootstrap = initializeBootstrap();

    private static Bootstrap initializeBootstrap() {
        eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.SO_KEEPALIVE, true);

        return bootstrap;
    }



    private static Logger logger = LoggerFactory.getLogger(ChannelProvider.class);

    public static Channel getChannel(String serviceName, CommonSerializer serializer,LoadBalance loadBalance) {
        List<Instance> instances = nacosProvider.selectInstances(serviceName, true);

        //TODO 负载均衡
        ConcurrentHashMap<String, LoadBalance> map = LoadBalanceCache.getLoadbalanceMap();
        map.put(serviceName, loadBalance);
        Instance instance = loadBalance.selectOne(instances);


        String ip = instance.getIp();
        int port = instance.getPort();

        String key = ip + port + serializer.getCode();
        Channel channel = channelMap.get(key);
        if (channelMap.contains(key)){
            Channel tmp = channelMap.get(key);
            if (tmp != null) return tmp;
            else
                channelMap.remove(key);
        }

        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                //得到pipeline
                ChannelPipeline pipeline = socketChannel.pipeline();
                //加入相关handler
                pipeline.addLast("decoder", new CommonDecoder());
                pipeline.addLast("encoder", new CommonEncoder(new KryoSerializer()));
                //加入自定义的handler
                pipeline.addLast(new ClientHandler());
            }
        });

        Channel res = null;
        try {
             res = connect(bootstrap, new InetSocketAddress(ip, port));
        }catch (Exception e){
            logger.error("客户端连接失败");
            throw new CommonException(CustomizeException.NETTY_CLIENT_TIMEOUT);
        }
        channelMap.put(key, res);
        return res;


    }

    public static Channel getChannel(String serviceName, CommonSerializer serializer) {
        List<Instance> instances = nacosProvider.selectInstances(serviceName, true);
        //TODO 负载均衡
        ConcurrentHashMap<String, LoadBalance> map = LoadBalanceCache.getLoadbalanceMap();

        LoadBalance loadBalance = null;
        if (map.containsKey(serviceName)){
            loadBalance = map.get(serviceName);
        }else{
            loadBalance = new RandomLoadBalance();
            map.put(serviceName, loadBalance);
        }
        Instance instance = loadBalance.selectOne(instances);
        logger.info("选择了："+instance.getPort());
        String ip = instance.getIp();
        int port = instance.getPort();

        String key = ip + port + serializer.getCode();
        Channel channel = channelMap.get(key);
        if (channelMap.contains(key)){
            Channel tmp = channelMap.get(key);
            if (tmp != null) return tmp;
            else
                channelMap.remove(key);
        }

        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                //得到pipeline
                ChannelPipeline pipeline = socketChannel.pipeline();
                //加入相关handler
                pipeline.addLast("decoder", new CommonDecoder());
                pipeline.addLast("encoder", new CommonEncoder(new KryoSerializer()));
                //加入自定义的handler
                pipeline.addLast(new ClientHandler());
            }
        });

        Channel res = null;
        try {
            res = connect(bootstrap, new InetSocketAddress(ip, port));
        }catch (Exception e){
            logger.error("客户端连接失败");
            throw new CommonException(CustomizeException.NETTY_CLIENT_TIMEOUT);
        }
        channelMap.put(key, res);
        return res;


    }

    private static Channel connect(Bootstrap bootstrap, InetSocketAddress inetSocketAddress) throws ExecutionException, InterruptedException {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                logger.info("客户端连接成功!");
                completableFuture.complete(future.channel());
            } else {
                throw new IllegalStateException();
            }
        });

        return completableFuture.get();
    }





}
