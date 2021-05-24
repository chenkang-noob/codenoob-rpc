package com.imnoob.transport.netty.server;

import io.netty.channel.nio.NioEventLoopGroup;

public class NettyServer {

    //TODO 启动服务  暴露接口
    private Integer port;
    private String serviceName;
    private String host;

    public NettyServer(Integer port, String serviceName, String host) {
        this.port = port;
        this.serviceName = serviceName;
        this.host = host;
    }

    public NettyServer() {
        this.port=9000;
        this.serviceName = "rpc-comsumer";
        this.host = "127.0.0.1";
    }


    public void run(){
        new NioEventLoopGroup();



    }
}
