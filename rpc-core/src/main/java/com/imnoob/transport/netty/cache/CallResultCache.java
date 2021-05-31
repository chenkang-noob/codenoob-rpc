package com.imnoob.transport.netty.cache;

import com.imnoob.transport.netty.loadbalance.LoadBalance;
import com.imnoob.transport.netty.model.RpcResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

public class CallResultCache {

    private  static volatile ConcurrentHashMap<String, CompletableFuture<RpcResponse>> resultMap;


    public static  ConcurrentHashMap<String, CompletableFuture<RpcResponse>> getResultMap(){
        if (resultMap == null){
            synchronized (LoadBalanceCache.class){
                if (resultMap == null) return resultMap = new ConcurrentHashMap<>();
            }
        }

        return resultMap;
    }

    public static CompletableFuture<RpcResponse>  get(String id) throws ExecutionException, InterruptedException {
        CompletableFuture<RpcResponse> future = resultMap.get(id);
        resultMap.remove(id);
        return future;
    }

    public static void put(String id, CompletableFuture<RpcResponse> res){
        resultMap.put(id,res);
    }

    public void remove(String requestId) {
        if (resultMap.containsKey(requestId)) {
            resultMap.remove(requestId);
        }
    }

}
