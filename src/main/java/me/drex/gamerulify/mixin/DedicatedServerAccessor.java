package me.drex.gamerulify.mixin;

import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.rcon.thread.QueryThreadGs4;
import net.minecraft.server.rcon.thread.RconThread;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DedicatedServer.class)
public interface DedicatedServerAccessor {

    @Accessor("queryThreadGs4")
    QueryThreadGs4 getQueryThread();

    @Accessor("queryThreadGs4")
    void setQueryThread(QueryThreadGs4 queryThread);

    @Accessor("rconThread")
    RconThread getRconThread();

    @Accessor("rconThread")
    void setRconThread(RconThread rconThread);

}
