package com.imnoob.transport.netty.provider.impl;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.pojo.Instance;

import com.imnoob.transport.netty.enums.CustomizeException;
import com.imnoob.transport.netty.exception.CommonException;
import com.imnoob.transport.netty.provider.RegisterProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NacosProvider implements RegisterProvider {

    private final static Logger logger = LoggerFactory.getLogger(NacosProvider.class);
    private String nacosIp;
    private Integer nacosPort;

    public NacosProvider(String nacosIp,Integer nacosPort) {
        this.nacosIp = nacosIp;
        this.nacosPort = nacosPort;
    }

    public NacosProvider() {
        nacosIp = "127.0.0.1";
        nacosPort = 8848;
    }

    @Override
    public void registerService(String serviceName, String ip, int port) {
        try {
            NamingService naming = NamingFactory.createNamingService(nacosIp);

            Instance instance = new Instance();
            instance.setIp(ip);
            instance.setPort(port);
            instance.setServiceName(serviceName);
            instance.setHealthy(true);
            instance.setWeight(2.0);
            Map<String, String> instanceMeta = new HashMap<>();
            instanceMeta.put("site", "et2");
            instance.setMetadata(instanceMeta);
            naming.registerInstance(serviceName,instance);
        }catch (NacosException exception){
            logger.error("Nacos 服务注册失败");
            throw new CommonException(CustomizeException.NACOS_TIMEOUT);
        }
    }

    @Override
    public void registerInstance(String serviceName, String ip, int port, String clusterName) {}

    @Override
    public void deregisterInstance(String serviceName, String ip, int port) {
        try {
            NamingService naming = NamingFactory.createNamingService(nacosIp);
            naming.deregisterInstance(serviceName, ip, port);
        }catch (NacosException e){
            logger.error("nacos 删除实例错误");
            throw new CommonException(CustomizeException.NACOS_TIMEOUT);
        }
    }

    @Override
    public List<Instance> selectInstances(String serviceName, boolean healthy) {
        try {
            NamingService naming = NamingFactory.createNamingService(nacosIp);
            return naming.selectInstances(serviceName, true);
        }catch (NacosException e){
            logger.error("获取健康实例错误");
            throw new CommonException(CustomizeException.NACOS_TIMEOUT);
        }

    }

    @Override
    public void subscribe(String serviceName, EventListener listener) {
        try {
            NamingService naming = NamingFactory.createNamingService(nacosIp);
            naming.subscribe(serviceName,listener);
        }catch (Exception e){
            logger.error("");

        }
    }


}
