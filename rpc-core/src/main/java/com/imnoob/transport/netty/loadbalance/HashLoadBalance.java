package com.imnoob.transport.netty.loadbalance;

import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.imnoob.transport.netty.model.RpcRequest;
import com.imnoob.transport.netty.provider.NacosProvider;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HashLoadBalance implements LoadBalance {

    private TreeMap<String, Instance> treeMap = new TreeMap<>();
    private String serviceName;
    private NacosProvider nacosProvider;

    public HashLoadBalance(String serviceName) {
        this.serviceName = serviceName;
        nacosProvider = new NacosProvider();
        List<Instance> instances = nacosProvider.selectInstances(serviceName,true);
        nacosProvider.subscribe(serviceName,event -> {
            if (event instanceof NamingEvent){
                NamingEvent tmp = (NamingEvent) event;
                List<Instance> list = tmp.getInstances();
                buildTree(list,treeMap);
            }
        });
        buildTree(instances,treeMap);
    }

    @Override
    public Instance selectOne(List<Instance> list) {
        return null;
    }

    private void buildTree(List<Instance> list,TreeMap<String, Instance> treeMap) {
        treeMap.clear();
        for (Instance instance : list) {
            String key = instance.getIp() + instance.getPort();
            treeMap.put(key, instance);
        }
    }

    public Instance selectOne(RpcRequest request) {
        Iterator<Map.Entry<String, Instance>> iterator = treeMap.entrySet().iterator();
        Integer target = request.hashCode();
        while (iterator.hasNext()){
            Map.Entry<String, Instance> next = iterator.next();
            if (next.getKey().hashCode() > target) return next.getValue();
        }

        return treeMap.firstEntry().getValue();
    }
}
