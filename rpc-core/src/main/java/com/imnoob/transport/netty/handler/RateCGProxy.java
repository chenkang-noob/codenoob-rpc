package com.imnoob.transport.netty.handler;

import com.google.common.util.concurrent.RateLimiter;
import com.imnoob.transport.netty.annotation.RateLimit;
import com.imnoob.transport.netty.cache.RateLimitCache;
import com.imnoob.transport.netty.enums.CustomizeException;
import com.imnoob.transport.netty.exception.CommonException;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;


//cglib 动态代理
public class RateCGProxy implements MethodInterceptor {

    private Enhancer enhancer = new Enhancer();
    private final RateLimiter rateLimiter;

    public RateCGProxy(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    public Object getProxy(Class clazz){

        //设置需要创建子类的类
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(this);
        //通过字节码技术动态创建子类实例
        return enhancer.create();
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {


        Object result = null;
        try {
            if (rateLimiter.tryAcquire()) {
                 result = methodProxy.invokeSuper(o, objects);
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw new CommonException(CustomizeException.LIMITE_RATE_ERROR);
        }


        return result;
    }
}
