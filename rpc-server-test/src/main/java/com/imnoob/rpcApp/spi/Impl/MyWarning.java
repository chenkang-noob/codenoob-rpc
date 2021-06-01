package com.imnoob.rpcApp.spi.Impl;

import com.imnoob.transport.netty.spi.Warning;

public class MyWarning implements Warning {
    static {
        System.out.println("MyWarning 静态方法块被加载 可以向容器添加组件");
    }

    @Override
    public String sendWarning(String msg) {
        System.out.println("Warning: "+msg);
        return "WARIN";
    }
}
