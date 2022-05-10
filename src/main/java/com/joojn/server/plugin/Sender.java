package com.joojn.server.plugin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ICommandListener;

public class Sender {

    private final ICommandListener listener;

    public Sender(ICommandListener listener){
        this.listener = listener;
    }

    public boolean isConsole(){
        String name = this.listener.getUsername();

        return name.equals("CONSOLE");
    }


    public EntityPlayerMP getPlayer(){
        return MinecraftServer.getMinecraftServer().configManager.getPlayer(this.listener.getUsername());
    }

    public void sendMessage(String message){
        this.listener.log(message);
    }
}
