package com.imnoob.transport.netty.loadbalance;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

public class RoundLoadBalance implements LoadBalance {
    //轮询 负载均衡
    private Integer index = 0;
    @Override
    public Instance selectOne(List<Instance> list) {
        Instance instance = null;
        if (index >= list.size()){
            index = index % list.size();
            instance = list.get(index);
        } else instance = list.get(index);
        index++;
        if (index == Integer.MAX_VALUE) index = 0;
        return instance;
    }
}
