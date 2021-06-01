package com.imnoob.rpcApp;

import com.imnoob.rpcApp.spi.Impl.MyWarning;
import com.imnoob.transport.netty.annotation.ServiceScan;
import com.imnoob.transport.netty.cache.RateLimitCache;
import com.imnoob.transport.netty.cache.ServiceCache;
import com.imnoob.transport.netty.server.NettyServer;
import com.imnoob.transport.netty.spi.Warning;
import com.imnoob.transport.netty.spi.WarningFactory;

import java.util.concurrent.ConcurrentHashMap;

@ServiceScan
public class ServerApplication {

    public static void main(String[] args) {
        NettyServer nettyServer = new NettyServer();
        try {
            nettyServer.run();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
