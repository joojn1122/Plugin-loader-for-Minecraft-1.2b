package com.joojn.server.event.impl;

import com.joojn.server.event.EventCancelable;
import net.minecraft.src.EntityPlayerMP;

public class BlockPlaceEvent extends EventCancelable {

    private final EntityPlayerMP player;

    public BlockPlaceEvent(EntityPlayerMP player) {
        this.player = player;
    }

    public EntityPlayerMP getPlayer(){
        return this.player;
    }
}
