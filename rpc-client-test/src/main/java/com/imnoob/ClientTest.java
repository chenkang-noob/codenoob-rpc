package com.imnoob;

import com.imnoob.rpcApp.service.ByeSerivce;
import com.imnoob.rpcApp.service.HelloService;
import com.imnoob.rpcApp.service.People;
import com.imnoob.transport.netty.client.ClientProxy;
import com.imnoob.transport.netty.client.NettyClient;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class ClientTest {

    public static void main(String[] args) throws Exception{

        NettyClient nettyClient = new NettyClient();
        ClientProxy proxy = new ClientProxy(nettyClient, "rpc-provider");


        People rs = new People(18,"张三");
        HelloService helloService = proxy.getProxy(HelloService.class);


        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                helloService.sayHello("hello");
            }
        });
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                helloService.sayHello("hello");
            }
        });
        Thread t3 = new Thread(new Runnable() {
            @Override
            public void run() {
                helloService.sayHello("hello");
            }
        });
        Thread t4 = new Thread(new Runnable() {
            @Override
            public void run() {
                helloService.sayHello("hello");
            }
        });
        Thread t5 = new Thread(new Runnable() {
            @Override
            public void run() {
                helloService.sayHello("hello");
            }
        });

        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();

        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();


    }
}
