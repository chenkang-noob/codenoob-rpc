package com.imnoob.rpcApp.service.Impl;

import com.imnoob.rpcApp.service.ByeSerivce;
import com.imnoob.rpcApp.service.People;
import com.imnoob.transport.netty.annotation.Service;

@Service
public class ByeServiceImpl implements ByeSerivce {
    @Override
    public String sayBye(People person) {
        return person.toString() + "say good bye";
    }
}
