package com.imnoob.transport.netty.loadbalance;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.Random;

public class RandomLoadBalance implements LoadBalance {

    //随机 负载
    @Override
    public Instance selectOne(List<Instance> list) {
        return list.get(new Random().nextInt(list.size()));
    }
}
