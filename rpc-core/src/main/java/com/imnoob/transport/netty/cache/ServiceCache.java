package com.imnoob.transport.netty.cache;

import com.imnoob.transport.netty.loadbalance.LoadBalance;

import java.util.concurrent.ConcurrentHashMap;

public class ServiceCache {

    private  static volatile ConcurrentHashMap<String, Object> serviceMap;


    public static  ConcurrentHashMap<String, Object> initMap(){
        if (serviceMap == null){
            synchronized (LoadBalanceCache.class){
                if (serviceMap == null)  serviceMap = new ConcurrentHashMap<>();
            }
        }

        return serviceMap;
    }

    public static void addService(String name,Object obj){
        if (serviceMap == null) initMap();
        serviceMap.put(name, obj);
    }
    public static Object findService(String name){
        if (serviceMap == null) initMap();
        return serviceMap.get(name);
    }
}
