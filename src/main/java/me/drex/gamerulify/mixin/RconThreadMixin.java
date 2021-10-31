package me.drex.gamerulify.mixin;

import me.drex.gamerulify.GameRulify;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerInterface;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.server.rcon.thread.RconThread;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RconThread.class)
public abstract class RconThreadMixin {

    @Redirect(
            method = "create",
            at = @At(
                    value = "FIELD",
                    opcode = Opcodes.GETFIELD,
                    target = "Lnet/minecraft/server/dedicated/DedicatedServerProperties;rconPassword:Ljava/lang/String;"
            )
    )
    private static String customRconPassword(DedicatedServerProperties dedicatedServerProperties, ServerInterface serverInterface) {
        if (serverInterface instanceof MinecraftServer server) {
            return server.getGameRules().getRule(GameRulify.RCON_PASSWORD).get();
        }
        return dedicatedServerProperties.rconPassword;
    }

    @Redirect(
            method = "create",
            at = @At(
                    value = "FIELD",
                    opcode = Opcodes.GETFIELD,
                    target = "Lnet/minecraft/server/dedicated/DedicatedServerProperties;rconPort:I"
            )
    )
    private static int customRconPort(DedicatedServerProperties dedicatedServerProperties, ServerInterface serverInterface) {
        if (serverInterface instanceof MinecraftServer server) {
            return server.getGameRules().getRule(GameRulify.RCON_PORT).get();
        }
        return dedicatedServerProperties.rconPort;
    }

}
