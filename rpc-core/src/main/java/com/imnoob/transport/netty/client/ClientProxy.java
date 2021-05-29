package com.imnoob.transport.netty.client;

import com.imnoob.transport.netty.model.RpcRequest;
import com.imnoob.transport.netty.model.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ClientProxy implements InvocationHandler {

    private final NettyClient client;
    private final String serviceName;
    public ClientProxy(NettyClient client,String serviceName) {
        this.client = client;
        this.serviceName = serviceName;
    }

    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString().replace("-",""));
        request.setInterfaceName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameters(args);
        request.setParamTypes(method.getParameterTypes());

        CompletableFuture<RpcResponse> futer = client.sendMsg(serviceName, request);
        return futer.get().getData();
    }
}
