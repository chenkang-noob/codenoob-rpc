package com.imnoob.rpcApp.spi.Impl;

import com.imnoob.transport.netty.spi.SpiTest;

public class MySpiTest implements SpiTest {
    @Override
    public void function1() {
        System.out.println("function1");
    }
}
