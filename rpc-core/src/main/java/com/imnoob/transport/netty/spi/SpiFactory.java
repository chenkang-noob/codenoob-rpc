package com.imnoob.transport.netty.spi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

public class SpiFactory {



    public static List<Object> getInstance(Class clazz) {
        ArrayList<Object> res = new ArrayList<>();
        Class[] interfaces = clazz.getInterfaces();

        for (Class item : interfaces) {
            ServiceLoader loader = ServiceLoader.load(item);
            Iterator iterator = loader.iterator();
            while (iterator.hasNext()){
                Object next = iterator.next();
                if (next.getClass().equals(clazz)) res.add(next);
            }
        }
        return res;


    }

}
