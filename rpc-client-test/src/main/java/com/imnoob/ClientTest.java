package com.imnoob;

import com.imnoob.rpcApp.service.ByeSerivce;
import com.imnoob.rpcApp.service.HelloService;
import com.imnoob.rpcApp.service.People;
import com.imnoob.transport.netty.client.ClientProxy;
import com.imnoob.transport.netty.client.NettyClient;

import java.util.concurrent.TimeUnit;

public class ClientTest {

    public static void main(String[] args) throws Exception{
        NettyClient nettyClient = new NettyClient();
        ClientProxy proxy = new ClientProxy(nettyClient, "rpc-provider");


        for (int i=0;i < 1;i++){
           new Thread(new Runnable() {
               @Override
               public void run() {
                   People rs = new People(18, "张三");
                   HelloService service = proxy.getProxy(HelloService.class);
                   People res = service.sayHello("hello");
                   System.out.println(res);
                   System.out.println("----------------------------");
               }
           }).start();
        }

//        ByeSerivce byeSerivce = proxy.getProxy(ByeSerivce.class);
//        String s = byeSerivce.sayBye(rs);
//        System.out.println(s);


    }
}
