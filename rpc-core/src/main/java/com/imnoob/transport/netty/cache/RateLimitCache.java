package com.imnoob.transport.netty.cache;

import com.google.common.util.concurrent.RateLimiter;

import java.util.concurrent.ConcurrentHashMap;

public class RateLimitCache {

    private  static volatile ConcurrentHashMap<String, RateLimiter> rateLimitMap;


    public static  ConcurrentHashMap<String, RateLimiter> initMap(){
        if (rateLimitMap == null){
            synchronized (LoadBalanceCache.class){
                if (rateLimitMap == null)  rateLimitMap = new ConcurrentHashMap<>();
            }
        }

        return rateLimitMap;
    }

    public static void addRateLimit(String name,RateLimiter obj){
        if (rateLimitMap == null) initMap();
        rateLimitMap.put(name, obj);
    }
    public static RateLimiter getRateLimit(String name){
        if (rateLimitMap == null) initMap();
        return rateLimitMap.get(name);
    }
}
