package me.drex.gamerulify.mixin;

import me.drex.gamerulify.GameRulify;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {

    @Shadow
    public abstract MinecraftServer getServer();

    /**
     * @author Drex
     * @reason Add "max-players" gamerule
     */
    @Overwrite
    public int getMaxPlayers() {
        return this.getServer().getGameRules().getInt(GameRulify.MAX_PLAYERS);
    }

}
