package com.imnoob.transport.netty.cache;

import com.imnoob.transport.netty.loadbalance.LoadBalance;

import java.util.concurrent.ConcurrentHashMap;

public class LoadBalanceCache {

    private  static volatile ConcurrentHashMap<String, LoadBalance> loadbalanceMap;


    public static  ConcurrentHashMap<String, LoadBalance> getLoadbalanceMap(){
        if (loadbalanceMap == null){
            synchronized (LoadBalanceCache.class){
                if (loadbalanceMap == null) return loadbalanceMap = new ConcurrentHashMap<>();
            }
        }

        return loadbalanceMap;
    }
}
