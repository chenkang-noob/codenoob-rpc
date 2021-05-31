package com.imnoob.rpcApp.service.Impl;

import com.imnoob.rpcApp.service.HelloService;
import com.imnoob.rpcApp.service.People;
import com.imnoob.transport.netty.annotation.RateLimit;
import com.imnoob.transport.netty.annotation.Service;

@Service
@RateLimit(value = 1)
public class HelloServiceImpl implements HelloService {
    @Override
    public People sayHello(String msg) {
        People people = new People(18,"张三");
        return people;
    }
}
