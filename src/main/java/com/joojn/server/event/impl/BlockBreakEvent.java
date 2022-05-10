package com.joojn.server.event.impl;

import com.joojn.server.event.EventCancelable;
import net.minecraft.src.EntityPlayerMP;

public class BlockBreakEvent extends EventCancelable {

    private final EntityPlayerMP player;

    public BlockBreakEvent(EntityPlayerMP player){
        this.player = player;
    }

    public EntityPlayerMP getPlayer(){
        return this.player;
    }
}
