package me.drex.gamerulify.mixin;

import me.drex.gamerulify.GameRulify;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * Notice:
 * We're using {@link Overwrite}, because the targeted methods are one-liners and this will alert us of any mod conflicts immediately.
 */

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Shadow
    public abstract ServerLevel overworld();

    @Shadow
    public abstract GameRules getGameRules();

    @Shadow
    public abstract boolean usesAuthentication();

    /**
     * @author Drex
     * @reason Add "pvp" gamerule
     */
    @Overwrite
    public boolean isPvpAllowed() {
        return this.getGameRules().getBoolean(GameRulify.PVP);
    }

    /**
     * @author Drex
     * @reason Add "allow-flight" gamerule
     */
    @Overwrite
    public boolean isFlightAllowed() {
        return this.getGameRules().getBoolean(GameRulify.ALLOW_FLIGHT);
    }

    /**
     * @author Drex
     * @reason Add "prevent-proxy-connections" gamerule
     */
    @Overwrite
    public boolean getPreventProxyConnections() {
        return this.getGameRules().getBoolean(GameRulify.PREVENT_PROXY_CONNECTIONS);
    }

    /**
     * Update onlineMode gamerule in case setUsesAuthentication called outside gamerule change callback.
     * @author Philip-Nicolas
     */
    @Inject(method = "setUsesAuthentication", at = @At("RETURN"))
    public void onSetUsesAuthentication(CallbackInfo ci) {
        // ensure level exists before calling getGameRules
        if (this.overworld() != null) {
            GameRules.BooleanValue onlineMode = this.getGameRules().getRule(GameRulify.ONLINE_MODE);
            if (this.usesAuthentication() != onlineMode.get()) {
                onlineMode.set(this.usesAuthentication(), (MinecraftServer) (Object) this);
            }
        }
    }

}
