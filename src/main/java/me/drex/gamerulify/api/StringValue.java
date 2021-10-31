package me.drex.gamerulify.api;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public class StringValue extends GameRules.Value<StringValue> {

    private String value;

    public static GameRules.Type<StringValue> create(String value, BiConsumer<MinecraftServer, StringValue> biConsumer) {
        return new GameRules.Type<>(StringArgumentType::greedyString, (type) -> new StringValue(type, value), biConsumer, (ignored, ignored2, ignored3) -> {});
    }

    public static GameRules.Type<StringValue> create(String value) {
        return create(value, (a, b) -> {});
    }

    public StringValue(GameRules.Type<StringValue> type, String value) {
        super(type);
        this.value = value;
    }

    @Override
    protected void updateFromArgument(CommandContext<CommandSourceStack> commandContext, String string) {
        this.value = StringArgumentType.getString(commandContext, string);
    }

    public String get() {
        // Allow empty strings
        if (this.value.equals("\"\"")) return "";
        return StringEscapeUtils.unescapeJava(this.value);
    }

    @Override
    protected void deserialize(String string) {
        this.value = string;
    }

    @Override
    public String serialize() {
        return this.value;
    }

    @Override
    public int getCommandResult() {
        return value.length();
    }

    @Override
    protected StringValue getSelf() {
        return this;
    }

    @Override
    protected StringValue copy() {
        return new StringValue(this.type, this.value);
    }

    @Override
    public void setFrom(StringValue value, @Nullable MinecraftServer minecraftServer) {
        this.value = value.value;
        this.onChanged(minecraftServer);
    }

}
