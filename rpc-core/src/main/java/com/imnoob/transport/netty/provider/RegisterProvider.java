package com.imnoob.transport.netty.provider;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

public interface RegisterProvider  {

    void registerService(String serviceName, String ip, int port);

    void registerInstance(String serviceName, String ip, int port, String clusterName);

    void deregisterInstance(String serviceName, String ip, int port) ;

    List<Instance> selectInstances(String serviceName, boolean healthy);
}
