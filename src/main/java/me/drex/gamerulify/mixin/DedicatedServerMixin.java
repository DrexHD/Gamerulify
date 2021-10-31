package me.drex.gamerulify.mixin;

import com.google.common.base.Strings;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import me.drex.gamerulify.GameRulify;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerResources;
import net.minecraft.server.dedicated.DedicatedPlayerList;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.server.dedicated.DedicatedServerSettings;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.Proxy;

/**
 * Notice:
 * We're using {@link Overwrite}, because the targeted methods are one-liners and this will alert us of any mod conflicts immediately.
 */

@Mixin(DedicatedServer.class)
public abstract class DedicatedServerMixin extends MinecraftServer {

    @Shadow public abstract String getPackHash();

    @Shadow public abstract DedicatedPlayerList getPlayerList();

    public DedicatedServerMixin(Thread thread, RegistryAccess.RegistryHolder registryHolder, LevelStorageSource.LevelStorageAccess levelStorageAccess, WorldData worldData, PackRepository packRepository, Proxy proxy, DataFixer dataFixer, ServerResources serverResources, @Nullable MinecraftSessionService minecraftSessionService, @Nullable GameProfileRepository gameProfileRepository, @Nullable GameProfileCache gameProfileCache, ChunkProgressListenerFactory chunkProgressListenerFactory) {
        super(thread, registryHolder, levelStorageAccess, worldData, packRepository, proxy, dataFixer, serverResources, minecraftSessionService, gameProfileRepository, gameProfileCache, chunkProgressListenerFactory);
    }

    /**
     * @author Drex
     * @reason Add "enable-command-block" gamerule
     */
    @Overwrite
    public boolean isCommandBlockEnabled() {
        return this.getGameRules().getBoolean(GameRulify.ENABLE_COMMAND_BLOCK);
    }

    /**
     * @author Drex
     * @reason Add "network-compression-threshold" gamerule
     */
    @Overwrite
    public int getCompressionThreshold() {
        return this.getGameRules().getInt(GameRulify.NETWORK_COMPRESSION_TRESHOLD);
    }

    /**
     * @author Drex
     * @reason Add "enable-status" gamerule
     */
    @Overwrite
    public boolean repliesToStatus() {
        return this.getGameRules().getBoolean(GameRulify.ENABLE_STATUS);
    }

    /**
     * @author Drex
     * @reason Add "broadcast-rcon-to-ops" gamerule
     */
    @Overwrite
    public boolean shouldRconBroadcast() {
        return this.getGameRules().getBoolean(GameRulify.BROADCAST_RCON_TO_OPS);
    }

    /**
     * @author Drex
     * @reason Add "allow-nether" gamerule
     */
    @Overwrite
    public boolean isNetherEnabled() {
        return this.getGameRules().getBoolean(GameRulify.ALLOW_NETHER);
    }

    /**
     * @author Drex
     * @reason Add "op-permission-level" gamerule
     */
    @Overwrite
    public int getOperatorUserPermissionLevel() {
        return this.getGameRules().getInt(GameRulify.OP_PERMISSION_LEVEL);
    }

    @Redirect(
            method = "getScaledTrackingDistance",
            at = @At(
                    value = "FIELD",
                    opcode = Opcodes.GETFIELD,
                    target = "Lnet/minecraft/server/dedicated/DedicatedServerProperties;entityBroadcastRangePercentage:I"
            )
    )
    public int customEntityBroadCastRangePercentage(DedicatedServerProperties dedicatedServerProperties) {
        return this.getGameRules().getInt(GameRulify.ENTITY_BROADCAST_RANGE_PERCENTAGE);
    }

    @Redirect(
            method = "getForcedGameType",
            at = @At(
                    value = "FIELD",
                    opcode = Opcodes.GETFIELD,
                    target = "Lnet/minecraft/server/dedicated/DedicatedServerProperties;forceGameMode:Z"
            )
    )
    public boolean customForceGameMode(DedicatedServerProperties dedicatedServerProperties) {
        return this.getGameRules().getBoolean(GameRulify.FORCE_GAMEMODE);
    }

    /**
     * @author Drex
     * @reason Add "rate-limit" gamerule
     */
    @Overwrite
    public int getRateLimitPacketsPerSecond() {
        return this.getGameRules().getInt(GameRulify.RATE_LIMIT);
    }

    /**
     * @author Drex
     * @reason Add "broadcast-console-to-ops" gamerule
     */
    @Overwrite
    public boolean shouldInformAdmins() {
        return this.getGameRules().getBoolean(GameRulify.BROADCAST_CONSOLE_TO_OPS);
    }

    @Redirect(
            method = "areNpcsEnabled",
            at = @At(
                    value = "FIELD",
                    opcode = Opcodes.GETFIELD,
                    target = "Lnet/minecraft/server/dedicated/DedicatedServerProperties;spawnNpcs:Z"
            )
    )
    public boolean customSpawnNpcs(DedicatedServerProperties dedicatedServerProperties) {
        return this.getGameRules().getBoolean(GameRulify.SPAWN_NPCS);
    }

    @Redirect(
            method = "isSpawningAnimals",
            at = @At(
                    value = "FIELD",
                    opcode = Opcodes.GETFIELD,
                    target = "Lnet/minecraft/server/dedicated/DedicatedServerProperties;spawnAnimals:Z"
            )
    )
    public boolean customSpawnAnimals(DedicatedServerProperties dedicatedServerProperties) {
        return this.getGameRules().getBoolean(GameRulify.SPAWN_ANIMALS);
    }

    @Redirect(
            method = "isSpawningMonsters",
            at = @At(
                    value = "FIELD",
                    opcode = Opcodes.GETFIELD,
                    target = "Lnet/minecraft/server/dedicated/DedicatedServerProperties;spawnMonsters:Z"
            )
    )
    public boolean customSpawnMonsters(DedicatedServerProperties dedicatedServerProperties) {
        return this.getGameRules().getBoolean(GameRulify.SPAWN_MONSTERS);
    }

    /**
     * @author Drex
     * @reason Add "function-permission-level" gamerule
     */
    @Overwrite
    public int getFunctionCompilationLevel() {
        return this.getGameRules().getInt(GameRulify.FUNCTION_PERMISSION_LEVEL);
    }

    /**
     * @author Drex
     * @reason Add "spawn-protection" gamerule
     */
    @Overwrite
    public int getSpawnProtectionRadius() {
        return this.getGameRules().getInt(GameRulify.SPAWN_PROTECTION);
    }

    @Redirect(
            method = "initServer",
            at = @At(
                    value = "FIELD",
                    opcode = Opcodes.GETFIELD,
                    target = "Lnet/minecraft/server/dedicated/DedicatedServerProperties;enableQuery:Z"
            )
    )
    public boolean customEnableQuery(DedicatedServerProperties dedicatedServerProperties) {
        return this.getGameRules().getBoolean(GameRulify.ENABLE_QUERY);
    }

    @Redirect(
            method = "initServer",
            at = @At(
                    value = "FIELD",
                    opcode = Opcodes.GETFIELD,
                    target = "Lnet/minecraft/server/dedicated/DedicatedServerProperties;enableRcon:Z"
            )
    )
    public boolean customEnableRcon(DedicatedServerProperties dedicatedServerProperties) {
        return this.getGameRules().getBoolean(GameRulify.ENABLE_RCON);
    }

    @Redirect(
            method = "initServer",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/server/dedicated/DedicatedServer;setMotd(Ljava/lang/String;)V"
            )
    )
    public void cancelMotdInit(DedicatedServer dedicatedServer, String string) {
        // noop: See below for more information
    }

    @Redirect(
            method = "initServer",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/server/dedicated/DedicatedServer;getPackHash()Ljava/lang/String;"
            )
    )
    public String cancelResourcePackInit(DedicatedServer dedicatedServer) {
        // noop: See below for more information
        return "";
    }

    @Inject(
            method = "initServer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/dedicated/DedicatedServer;loadLevel()V",
                    shift = At.Shift.AFTER
            )
    )
    public void customResourcePackInit(CallbackInfoReturnable<Boolean> cir) {
        // Overworld needs to be loaded to query gamerules
        this.setResourcePack(this.getGameRules().getRule(GameRulify.RESOURCE_PACK).get(), this.getPackHash());
        this.setMotd(this.getGameRules().getRule(GameRulify.MOTD).get());
        this.getPlayerList().setViewDistance(this.getGameRules().getInt(GameRulify.VIEW_DISTANCE));
    }

    @Redirect(
            method = "getPackHash",
            at = @At(
                    value = "FIELD",
                    opcode = Opcodes.GETFIELD,
                    target = "Lnet/minecraft/server/dedicated/DedicatedServerProperties;resourcePackSha1:Ljava/lang/String;"
            )
    )
    public String customResourcePackSha1(DedicatedServerProperties dedicatedServerProperties) {
        return this.getGameRules().getRule(GameRulify.RESOURCE_PACK_SHA1).get();
    }

    /**
    * This {@link Redirect} prevents {@link DedicatedServer#parseResourcePackPrompt} from running to remove any logs caused by invalid server property values.
    * */
    @Redirect(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/dedicated/DedicatedServer;parseResourcePackPrompt(Lnet/minecraft/server/dedicated/DedicatedServerSettings;)Lnet/minecraft/network/chat/Component;"
            )
    )
    public Component cancelResourcePackPrompt(DedicatedServerSettings dedicatedServerSettings) {
        return TextComponent.EMPTY;
    }

    /**
     * @author Drex
     * @reason Add "resource-pack-prompt" gamerule
     */
    @Nullable
    @Overwrite
    public Component getResourcePackPrompt() {
        String prompt = this.getGameRules().getRule(GameRulify.RESOURCE_PACK_PROMPT).get();
        if (!Strings.isNullOrEmpty(prompt)) {
            try {
                return Component.Serializer.fromJson(prompt);
            } catch (Exception ex) {
                GameRulify.LOGGER.warn("Failed to parse resource pack prompt '{}'", prompt, ex);
            }
        }
        return null;
    }

    /**
     * @author Drex
     * @reason Add "require-resource-pack" gamerule
     */
    @Overwrite
    public boolean isResourcePackRequired() {
        return this.getGameRules().getBoolean(GameRulify.RESOURCE_PACK_REQUIRE);
    }

}
