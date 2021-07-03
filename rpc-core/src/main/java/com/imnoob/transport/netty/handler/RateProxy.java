package com.imnoob.transport.netty.handler;

import com.google.common.util.concurrent.RateLimiter;

import com.imnoob.transport.netty.enums.CustomizeException;
import com.imnoob.transport.netty.exception.CommonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


public class RateProxy implements InvocationHandler {

    public RateProxy(Object rateService,RateLimiter rateLimiter) {
        this.rateService = rateService;
        this.rateLimiter = rateLimiter;
    }

    private final Object rateService;
    private final RateLimiter rateLimiter;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            if (rateLimiter.tryAcquire()) {
                logger.info("通过:" + this.hashCode() + "  hasocode :"+rateLimiter.hashCode());
                Object res = method.invoke(rateService, args);
                return res;
            }else{
                logger.info("拦截11111111:");
            }
        } catch (Throwable throwable) {

            throwable.printStackTrace();
            throw new CommonException(CustomizeException.LIMITE_RATE_ERROR);
        }
        return null;
    }
}
