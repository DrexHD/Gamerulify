package me.drex.gamerulify.mixin;

import me.drex.gamerulify.GameRulify;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerInterface;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.server.rcon.thread.QueryThreadGs4;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(QueryThreadGs4.class)
public abstract class QueryThreadGs4Mixin {

    @Redirect(
            method = "create",
            at = @At(
                    value = "FIELD",
                    opcode = Opcodes.GETFIELD,
                    target = "Lnet/minecraft/server/dedicated/DedicatedServerProperties;queryPort:I"
            )
    )
    private static int customQueryPort(DedicatedServerProperties dedicatedServerProperties, ServerInterface serverInterface) {
        if (serverInterface instanceof MinecraftServer server) {
            return server.getGameRules().getRule(GameRulify.QUERY_PORT).get();
        }
        return dedicatedServerProperties.queryPort;
    }

}
