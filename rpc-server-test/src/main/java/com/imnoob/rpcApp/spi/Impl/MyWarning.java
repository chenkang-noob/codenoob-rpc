package com.imnoob.rpcApp.spi.Impl;

import com.imnoob.transport.netty.spi.Warning;

public class MyWarning implements Warning {
    @Override
    public String sendWarning(String msg) {
        System.out.println("Warning: "+msg);
        return "WARIN";
    }
}
