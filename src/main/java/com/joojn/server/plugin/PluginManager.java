package com.joojn.server.plugin;

import com.joojn.server.event.EventManager;
import com.joojn.server.event.EventTarget;
import com.joojn.server.event.impl.StopEvent;
import com.joojn.server.event.impl.StartEvent;
import net.minecraft.src.ICommandListener;
import net.minecraft.src.ServerCommand;

import java.io.File;
import java.util.*;

public class PluginManager {

    private final PluginLoader loader;

    private PluginManager(){
        this.loader = new PluginLoader(new File("plugins"));
    }

    private static final PluginManager instance = new PluginManager();
    public static PluginManager getPluginManager() {return instance;}


    private final ArrayList<CommandExecutor> commands = new ArrayList<>();

    public void registerCommand(CommandExecutor executor){
        this.commands.add(executor);
    }

    public void unregisterCommand(String command){
        List<CommandExecutor> remove = new ArrayList<>();

        for(CommandExecutor executor : this.commands){
            for(String name : executor.getAliases()){
                if(name.equalsIgnoreCase(command)){
                    remove.add(executor);
                    break;
                }
            }
        }

        remove.forEach(this.commands::remove);
    }

    private final HashMap<MinecraftPlugin, EventListener> listeners = new HashMap<>();

    public void registerEvents(EventListener eventListener, MinecraftPlugin plugin){
        if(listeners.get(plugin) != null) return;

        EventManager.register(eventListener);
        listeners.put(plugin, eventListener);
    }

    public void unregisterEvents(MinecraftPlugin plugin){

        EventListener listener = listeners.get(plugin);
        if(listener == null) return;

        EventManager.unregister(listener);
        listeners.remove(plugin);

    }

    private Set<String> plugins = new HashSet<>();

    @EventTarget
    public void onStart(StartEvent event){

        this.reloadPlugins();
    }

    public void reloadPlugins(){
        try {

            this.loader.setupPlugins();
            this.plugins = this.loader.getObjects().keySet();
            this.loader.runMethods("onEnable");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Set<String> getPlugins(){
        return this.plugins;
    }

    public boolean disablePlugin(String plugin){

        try
        {
            return this.loader.runMethod("onDisable", plugin);
        }
        catch (Exception e)
        {
            return false;

        }

    }

    @EventTarget
    public void onDisable(StopEvent event){

        try {

            this.loader.runMethods("onDisable");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean sendCommand(ServerCommand servercommand) {
        String cmd = servercommand.command;
        String command = cmd.split(" ")[0];

        String[] argsFull = cmd.split(" ");
        String[] args = Arrays.copyOfRange(argsFull, 1, argsFull.length);

        ICommandListener listener = servercommand.commandListener;
        boolean found = false;

        for(CommandExecutor executor : this.commands){
            String[] aliases = executor.getAliases();

            for(String alias : aliases){
                if(alias.equalsIgnoreCase(command)){
                    executor.onCommand(new Sender(listener), alias, args);
                    found = true;
                    break;
                }
            }
        }

        return found;
    }
}