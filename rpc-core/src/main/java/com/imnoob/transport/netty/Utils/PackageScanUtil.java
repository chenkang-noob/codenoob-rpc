package com.imnoob.transport.netty.Utils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PackageScanUtil {

    public static Set<Class<?>> getClasses(String packageName) throws IOException, ClassNotFoundException {
        Set<Class<?>> res = new HashSet<>();

        String path = packageName.replace(".", "/");
        Enumeration<URL> dir = Thread.currentThread().getContextClassLoader().getResources(path);

        while (dir.hasMoreElements()){
            URL url = dir.nextElement();
            String protocol = url.getProtocol();
            if (protocol.equals("file")) {
                dealFile(packageName,new File(url.getFile()),res);
            }else if (protocol.equals("jar")){
                dealJar(url, res);
            }

        }
        return res;

    }

    private static void dealJar(URL url,Set<Class<?>> res) throws IOException, ClassNotFoundException {
        JarURLConnection urlConnection = (JarURLConnection) url.openConnection();
        JarFile jarFile = urlConnection.getJarFile();

        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()){
            JarEntry jarEntry = entries.nextElement();
            if (jarEntry.isDirectory() || !jarEntry.getName().endsWith(".class")){
                continue;
            }
            String jarName = jarEntry.getName();
            jarName = jarName.replace(".", "/");
            jarName = jarName.replace(".class", "");

            Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(jarName);
            res.add(clazz);
        }
    }

    private static void dealFile(String packageName, File file,Set<Class<?>> res) throws ClassNotFoundException {
        if (!file.exists() || !file.isDirectory()) return;

        File[] files = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.isDirectory() || pathname.getName().endsWith(".class")) return true;
                else return false;
            }
        });

        for (File item : files) {
            if (item.isDirectory()){
                dealFile(packageName+"."+item.getName(),item,res);
            }else if (item.isFile()){
                String name = item.getName().substring(0,item.getName().length()-6);
                res.add(Thread.currentThread().getContextClassLoader().loadClass(packageName+"."+name));
            }
        }

    }
}
