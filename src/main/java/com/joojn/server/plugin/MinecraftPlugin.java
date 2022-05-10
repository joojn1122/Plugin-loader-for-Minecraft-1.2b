package com.joojn.server.plugin;

public interface MinecraftPlugin {

    public void onEnable();

    public void onDisable();

    public default String getName(){
        return this.getClass().getSimpleName();
    }

}
