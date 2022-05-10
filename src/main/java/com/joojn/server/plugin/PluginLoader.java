package com.joojn.server.plugin;

import net.minecraft.server.MinecraftServer;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginLoader {

    private final File pluginsDir;

    public PluginLoader(File pluginsDir){
        this.pluginsDir = pluginsDir;
    }

    private final TreeMap<String, List<Object>> objects = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public void setupPlugins(){
        objects.clear();

        if(!pluginsDir.exists()) pluginsDir.mkdirs();

        for(String file : pluginsDir.list()){
            if(!file.toLowerCase().endsWith(".jar")) continue;

            File plugin = new File(pluginsDir, file);
            String pluginName = file.substring(0, file.length() - 4);

            MinecraftServer.getMinecraftServer().log("Loading " + file);


            try{

                List<Class<?>> classes = getClassesFromJar(plugin);
                List<Object> objects = getClassesFromInterfaceName(MinecraftPlugin.class, classes);

                this.objects.put(pluginName, objects);

                MinecraftServer.getMinecraftServer().log("Successfully loaded " + file);

            } catch (Exception e){

                MinecraftServer.getMinecraftServer().log("Failed while loading " + pluginName);
                MinecraftServer.getMinecraftServer().log(e.toString());

            }
        }
    }

    public TreeMap<String, List<Object>> getObjects(){
        return this.objects;
    }

    public boolean runMethod(String m, String plugin) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        List<Object> objects = this.objects.get(plugin);

        if(objects == null) return false;

        for(Object o : objects){

            Class<?> c = o.getClass();
            Method method = c.getDeclaredMethod(m);
            method.invoke(o);

        }

        return true;
    }

    public void runMethods(String m) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {

        for(String name : this.objects.keySet()){

            System.out.printf("Running %s at %s\n", m, name);

            this.runMethod(m, name);
        }

    }

    public List<Class<?>> getClassesFromJar(File jar) throws IOException, ClassNotFoundException {

        List<Class<?>> classes = new ArrayList<>();

        URI uri = jar.toURI();
        URL[] urls = new URL[] {uri.toURL()};
        ClassLoader classloader = new URLClassLoader(urls);

        JarFile jarFile = new JarFile(jar);
        Enumeration<JarEntry> e = jarFile.entries();

        while (e.hasMoreElements()) {

            JarEntry je = e.nextElement();
            if (je.isDirectory() || !je.getName().endsWith(".class")) continue;

            // -6 because of .class
            String className = je.getName().substring(0, je.getName().length() - 6);
            className = className.replace('/', '.');

            Class<?> c = classloader.loadClass(className);
            classes.add(c);

        }

        return classes;
    }

    public List<Object> getClassesFromInterfaceName(Class<?> inter, List<Class<?>> classes){

        List<Object> classesList = new ArrayList<>();

        for(Class<?> clazz : classes){
            Class<?>[] interfaces = clazz.getInterfaces();

            for(Class<?> in : interfaces){
                if(in.getName().equalsIgnoreCase(inter.getName())){
                    try {
                        classesList.add(clazz.newInstance());
                        break;
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }

        return classesList;
    }
}
