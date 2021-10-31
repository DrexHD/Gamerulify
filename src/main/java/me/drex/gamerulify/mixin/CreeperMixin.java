package me.drex.gamerulify.mixin;

import me.drex.gamerulify.GameRulify;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Creeper.class)
public abstract class CreeperMixin {

    @Redirect(
            method = "explodeCreeper",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"
            )
    )
    public boolean addCreeperGriefingGameRule(GameRules gameRules, GameRules.Key<GameRules.BooleanValue> key) {
        return gameRules.getBoolean(key) && gameRules.getBoolean(GameRulify.CREEPER_GRIEFING);
    }

}
