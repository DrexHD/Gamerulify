package me.drex.gamerulify;

import me.drex.gamerulify.api.StringValue;
import me.drex.gamerulify.mixin.DedicatedServerAccessor;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.gamerule.v1.CustomGameRuleCategory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.rcon.thread.QueryThreadGs4;
import net.minecraft.server.rcon.thread.RconThread;
import net.minecraft.world.level.GameRules;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameRulify implements DedicatedServerModInitializer {

    public static final Logger LOGGER = LogManager.getLogger();
    public static MinecraftServer minecraftServer;

    // Category
    public static final CustomGameRuleCategory API_CATEGORY = new CustomGameRuleCategory(new ResourceLocation("gamerulify", "api"), new TextComponent("API").withStyle(ChatFormatting.BOLD, ChatFormatting.YELLOW));
    public static final CustomGameRuleCategory RESOURCE_PACK_CATEGORY = new CustomGameRuleCategory(new ResourceLocation("gamerulify", "resource_pack"), new TextComponent("ResourcePack").withStyle(ChatFormatting.BOLD, ChatFormatting.YELLOW));
    public static final CustomGameRuleCategory SPAWNING_CATEGORY = new CustomGameRuleCategory(new ResourceLocation("gamerulify", "spawning"), new TextComponent("Spawning").withStyle(ChatFormatting.BOLD, ChatFormatting.YELLOW));
    public static final CustomGameRuleCategory ANTI_CHEAT_CATEGORY = new CustomGameRuleCategory(new ResourceLocation("gamerulify", "anti_cheat"), new TextComponent("AntiCheat").withStyle(ChatFormatting.BOLD, ChatFormatting.YELLOW));
    public static final CustomGameRuleCategory SERVER_CATEGORY = new CustomGameRuleCategory(new ResourceLocation("gamerulify", "server"), new TextComponent("Server").withStyle(ChatFormatting.BOLD, ChatFormatting.YELLOW));

    // API
    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_STATUS = register("enableStatus", API_CATEGORY, GameRuleFactory.createBooleanRule(true));
    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_RCON = register("enableRcon", API_CATEGORY, GameRuleFactory.createBooleanRule(false, GameRulify::toggleRcon));
    public static final GameRules.Key<GameRules.IntegerValue> RCON_PORT = register("rconPort", API_CATEGORY, GameRuleFactory.createIntRule(25565, 0, 65535, (server, ignored) -> restartRconThreadIfRunning(server)));
    public static final GameRules.Key<StringValue> RCON_PASSWORD = register("rconPassword", API_CATEGORY, StringValue.create("", (server, ignored) -> restartRconThreadIfRunning(server)));
    public static final GameRules.Key<GameRules.IntegerValue> QUERY_PORT = register("queryPort", API_CATEGORY, GameRuleFactory.createIntRule(25565, 0, 65535, (server, ignored) -> restartQueryThreadIfRunning(server)));
    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_QUERY = register("enableQuery", API_CATEGORY, GameRuleFactory.createBooleanRule(false, GameRulify::toggleQuery));

    // Spawning
    public static final GameRules.Key<GameRules.BooleanValue> SPAWN_NPCS = register("spawnNpcs", SPAWNING_CATEGORY, GameRuleFactory.createBooleanRule(true));
    public static final GameRules.Key<GameRules.BooleanValue> SPAWN_ANIMALS = register("spawnAnimals", SPAWNING_CATEGORY, GameRuleFactory.createBooleanRule(true));
    public static final GameRules.Key<GameRules.BooleanValue> SPAWN_MONSTERS = register("spawnMonsters", SPAWNING_CATEGORY, GameRuleFactory.createBooleanRule(true));

    // Resource pack
    public static final GameRules.Key<StringValue> RESOURCE_PACK = register("resourcePack", RESOURCE_PACK_CATEGORY, StringValue.create("", GameRulify::updateResourcePack));
    public static final GameRules.Key<StringValue> RESOURCE_PACK_SHA1 = register("resourcePackSha1", RESOURCE_PACK_CATEGORY, StringValue.create("", GameRulify::updateResourcePack));
    public static final GameRules.Key<GameRules.BooleanValue> RESOURCE_PACK_REQUIRE = register("requireResourcePack", RESOURCE_PACK_CATEGORY, GameRuleFactory.createBooleanRule(false));
    public static final GameRules.Key<StringValue> RESOURCE_PACK_PROMPT = register("resourcePackPrompt", RESOURCE_PACK_CATEGORY, StringValue.create(""));

    // AntiCheat
    public static final GameRules.Key<GameRules.IntegerValue> RATE_LIMIT = register("rateLimit", ANTI_CHEAT_CATEGORY, GameRuleFactory.createIntRule(0));
    public static final GameRules.Key<GameRules.BooleanValue> ALLOW_FLIGHT = register("allowFlight", ANTI_CHEAT_CATEGORY, GameRuleFactory.createBooleanRule(false));
    public static final GameRules.Key<GameRules.BooleanValue> PREVENT_PROXY_CONNECTIONS = register("preventProxyConnections", ANTI_CHEAT_CATEGORY, GameRuleFactory.createBooleanRule(false));

    // Server configuration
    public static final GameRules.Key<GameRules.IntegerValue> NETWORK_COMPRESSION_TRESHOLD = register("networkCompressionThreshold", SERVER_CATEGORY, GameRuleFactory.createIntRule(256, -1, 65535));
    public static final GameRules.Key<GameRules.IntegerValue> OP_PERMISSION_LEVEL = register("opPermissionLevel", SERVER_CATEGORY, GameRuleFactory.createIntRule(4, 1, 4));
    public static final GameRules.Key<GameRules.IntegerValue> FUNCTION_PERMISSION_LEVEL = register("functionPermissionLevel", SERVER_CATEGORY, GameRuleFactory.createIntRule(2, 1, 4));
    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_COMMAND_BLOCK = register("enableCommandBlock", SERVER_CATEGORY, GameRuleFactory.createBooleanRule(true));
    public static final GameRules.Key<GameRules.BooleanValue> BROADCAST_CONSOLE_TO_OPS = register("broadcastConsoleToOps", SERVER_CATEGORY, GameRuleFactory.createBooleanRule(true));
    public static final GameRules.Key<GameRules.IntegerValue> MAX_PLAYERS = register("maxPlayers", SERVER_CATEGORY, GameRuleFactory.createIntRule(20));
    public static final GameRules.Key<GameRules.BooleanValue> FORCE_GAMEMODE = register("forceGamemode", SERVER_CATEGORY, GameRuleFactory.createBooleanRule(false));
    public static final GameRules.Key<GameRules.IntegerValue> ENTITY_BROADCAST_RANGE_PERCENTAGE = register("entityBroadcastRangePercentage", SERVER_CATEGORY, GameRuleFactory.createIntRule(100, 10, 1000));
    public static final GameRules.Key<GameRules.BooleanValue> ALLOW_NETHER = register("allowNether", SERVER_CATEGORY, GameRuleFactory.createBooleanRule(true));
    public static final GameRules.Key<GameRules.IntegerValue> SPAWN_PROTECTION = register("spawnProtection", SERVER_CATEGORY, GameRuleFactory.createIntRule(16, 0));
    public static final GameRules.Key<GameRules.BooleanValue> BROADCAST_RCON_TO_OPS = register("broadcastRconToOps", SERVER_CATEGORY, GameRuleFactory.createBooleanRule(true));
    public static final GameRules.Key<GameRules.BooleanValue> PVP = register("pvp", SERVER_CATEGORY, GameRuleFactory.createBooleanRule(true));
    public static final GameRules.Key<StringValue> MOTD = register("motd", SERVER_CATEGORY, StringValue.create("A Minecraft Server", GameRulify::updateMotd));
    public static final GameRules.Key<GameRules.IntegerValue> VIEW_DISTANCE = register("viewDistance", SERVER_CATEGORY, GameRuleFactory.createIntRule(10, 2, 32, GameRulify::setViewDistance));
    public static final GameRules.Key<GameRules.BooleanValue> CREEPER_GRIEFING = register("creeperGriefing", SERVER_CATEGORY, GameRuleFactory.createBooleanRule(true));

    private static <T extends GameRules.Value<T>> GameRules.Key<T> register(String name, CustomGameRuleCategory category, GameRules.Type<T> type) {
        return GameRuleRegistry.register(name, category, type);
    }

    @Override
    public void onInitializeServer() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> minecraftServer = server);
    }

    private static void toggleQuery(MinecraftServer server, GameRules.BooleanValue gameRule) {
        final boolean queryEnabled = gameRule.get();
        if (server instanceof DedicatedServer dedicatedServer) {
            if (queryEnabled) {
                ((DedicatedServerAccessor) dedicatedServer).setQueryThread(QueryThreadGs4.create(dedicatedServer));
            } else {
                ((DedicatedServerAccessor) dedicatedServer).getQueryThread().stop();
                ((DedicatedServerAccessor) dedicatedServer).setQueryThread(null);
            }
        }
    }

    private static void toggleRcon(MinecraftServer server, GameRules.BooleanValue gameRule) {
        final boolean rconEnabled = gameRule.get();
        if (server instanceof DedicatedServer dedicatedServer) {
            final RconThread rconThread = ((DedicatedServerAccessor) dedicatedServer).getRconThread();
            if (rconEnabled) {
                if (rconThread != null) {
                    GameRulify.LOGGER.info("Rcon is already enabled!");
                } else {
                    ((DedicatedServerAccessor) dedicatedServer).setRconThread(RconThread.create(dedicatedServer));
                }
            } else {
                if (rconThread != null) {
                    ((DedicatedServerAccessor) dedicatedServer).getRconThread().stop();
                    ((DedicatedServerAccessor) dedicatedServer).setRconThread(null);
                } else {
                    GameRulify.LOGGER.info("Rcon is already disabled!");
                }
            }
        }
    }

    private static void restartQueryThreadIfRunning(MinecraftServer server) {
        if (server instanceof DedicatedServer dedicatedServer) {
            final QueryThreadGs4 queryThreadGs4 = ((DedicatedServerAccessor) dedicatedServer).getQueryThread();
            if (queryThreadGs4 != null) {
                ((DedicatedServerAccessor) dedicatedServer).getQueryThread().stop();
                final QueryThreadGs4 newQueryThreadGs4 = QueryThreadGs4.create(dedicatedServer);
                dedicatedServer.getGameRules().getRule(ENABLE_QUERY).set(newQueryThreadGs4 != null, server);
                ((DedicatedServerAccessor) dedicatedServer).setQueryThread(newQueryThreadGs4);
            }
        }
    }

    private static void restartRconThreadIfRunning(MinecraftServer server) {
        if (server instanceof DedicatedServer dedicatedServer) {
            final RconThread rconThread = ((DedicatedServerAccessor) dedicatedServer).getRconThread();
            if (rconThread != null) {
                ((DedicatedServerAccessor) dedicatedServer).getRconThread().stop();
                final RconThread newRconThread = RconThread.create(dedicatedServer);
                dedicatedServer.getGameRules().getRule(ENABLE_RCON).set(newRconThread != null, server);
                ((DedicatedServerAccessor) dedicatedServer).setRconThread(newRconThread);
            }
        }
    }

    private static void updateResourcePack(MinecraftServer server, StringValue gameRule) {
        if (server instanceof DedicatedServer dedicatedServer) {
            dedicatedServer.setResourcePack(dedicatedServer.getGameRules().getRule(RESOURCE_PACK).get(), dedicatedServer.getPackHash());
        }
    }

    private static void updateMotd(MinecraftServer server, StringValue gameRule) {
        if (server instanceof DedicatedServer dedicatedServer) {
            final String motd = gameRule.get();
            dedicatedServer.setMotd(motd);
            dedicatedServer.getStatus().setDescription(new TextComponent(motd));
        }
    }

    private static void setViewDistance(MinecraftServer server, GameRules.IntegerValue gameRule) {
        final int viewDistance = gameRule.get();
        server.getPlayerList().setViewDistance(viewDistance);
    }

}
