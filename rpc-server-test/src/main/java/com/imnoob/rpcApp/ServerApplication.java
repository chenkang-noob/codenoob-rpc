package com.imnoob.rpcApp;

import com.imnoob.rpcApp.spi.Impl.MyWarning;
import com.imnoob.transport.netty.annotation.ServiceScan;
import com.imnoob.transport.netty.cache.RateLimitCache;
import com.imnoob.transport.netty.cache.ServiceCache;
import com.imnoob.transport.netty.provider.NacosProvider;
import com.imnoob.transport.netty.serializer.ProtSerializer;
import com.imnoob.transport.netty.server.NettyServer;
import com.imnoob.transport.netty.spi.Warning;
import com.imnoob.transport.netty.spi.WarningFactory;

import java.util.concurrent.ConcurrentHashMap;

@ServiceScan
public class ServerApplication {

    public static void main(String[] args) {
        NacosProvider nacosProvider = new NacosProvider("127.0.0.1",8848);
        NettyServer nettyServer = new NettyServer("rpc-provider", "127.0.0.1", 9000, nacosProvider,new ProtSerializer());
//        NettyServer nettyServer = new NettyServer();
        try {
            nettyServer.run();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //SPI 机制
//        WarningFactory.getWarning(MyWarning.class);


    }
}
