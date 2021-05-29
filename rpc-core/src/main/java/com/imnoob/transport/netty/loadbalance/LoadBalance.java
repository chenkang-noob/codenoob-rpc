package com.imnoob.transport.netty.loadbalance;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.imnoob.transport.netty.model.RpcRequest;

import java.util.List;

public interface LoadBalance  {

    Instance selectOne(List<Instance> list);


}
