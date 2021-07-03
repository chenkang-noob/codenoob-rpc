package com.imnoob.rpcApp;

import com.imnoob.transport.netty.annotation.ServiceScan;
import com.imnoob.transport.netty.provider.impl.NacosProvider;
import com.imnoob.transport.netty.serializer.ProtSerializer;
import com.imnoob.transport.netty.server.NettyServer;

@ServiceScan
public class ServerApplication {

    public static void main(String[] args) {
        NacosProvider nacosProvider = new NacosProvider("127.0.0.1",8848);
        NettyServer nettyServer = new NettyServer("rpc-provider", "127.0.0.1", 9000, nacosProvider,new ProtSerializer());
        try {
            nettyServer.run();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //SPI 机制
//        List<Object> instance = SpiFactory.getInstance(MyWarning.class);
//        System.out.println(instance);
//        List<Object> test = SpiFactory.getInstance(MySpiTest.class);
//        System.out.println(test);

//        RateLimiter rateLimiter = RateLimiter.create(1);
//        for (int i=0 ; i < 5;i++){
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    for (int i=0; i <5;i++){
//                        if (rateLimiter.tryAcquire()){
//                            System.out.println("通过");
//                            try {
//                                Thread.sleep(100);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }else{
//                            System.out.println("拦截");
//                        }
//                    }
//                }
//            }).start();
//        }


    }
}
