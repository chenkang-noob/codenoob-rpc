package com.imnoob;

import com.imnoob.rpcApp.service.ByeSerivce;
import com.imnoob.rpcApp.service.HelloService;
import com.imnoob.rpcApp.service.People;
import com.imnoob.transport.netty.client.ClientProxy;
import com.imnoob.transport.netty.client.NettyClient;

public class ClientTest {

    public static void main(String[] args) throws Exception{
        NettyClient nettyClient = new NettyClient();
        ClientProxy proxy = new ClientProxy(nettyClient, "rpc-provider");

        People rs = new People(18, "张三");
//        HelloService service = proxy.getProxy(HelloService.class);
//        People res = service.sayHello("hello");
//        System.out.println(res);
//        System.out.println("----------------------------");

        ByeSerivce byeSerivce = proxy.getProxy(ByeSerivce.class);
        String s = byeSerivce.sayBye(rs);
        System.out.println(s);


    }
}
