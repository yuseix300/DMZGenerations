package com.yuseix300.dmzgenerations.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.yuseix300.dmzgenerations.age.AgeBonuses;
import com.yuseix300.dmzgenerations.age.AgeCapability;
import com.yuseix300.dmzgenerations.age.AgeData;
import com.yuseix300.dmzgenerations.age.AgeSettings;
import com.yuseix300.dmzgenerations.age.LifeStage;
import com.yuseix300.dmzgenerations.age.Rebirth;
import com.yuseix300.dmzgenerations.network.GenNetwork;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.List;

public final class AgeCommand {

    private AgeCommand() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("dmzgen")
                .requires(src -> src.hasPermission(2))
                .then(Commands.literal("age")
                        .then(Commands.literal("get")
                                .executes(ctx -> get(ctx.getSource(), List.of(self(ctx))))
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .executes(ctx -> get(ctx.getSource(), EntityArgument.getPlayers(ctx, "targets")))))
                        .then(Commands.literal("set")
                                // value first, target(s) optional at the end (defaults to self)
                                .then(Commands.argument("years", DoubleArgumentType.doubleArg(0))
                                        .executes(ctx -> setAge(ctx.getSource(), List.of(self(ctx)),
                                                DoubleArgumentType.getDouble(ctx, "years")))
                                        .then(Commands.argument("targets", EntityArgument.players())
                                                .executes(ctx -> setAge(ctx.getSource(),
                                                        EntityArgument.getPlayers(ctx, "targets"),
                                                        DoubleArgumentType.getDouble(ctx, "years"))))))
                        .then(Commands.literal("add")
                                .then(Commands.argument("years", DoubleArgumentType.doubleArg())
                                        .executes(ctx -> addAge(ctx.getSource(), List.of(self(ctx)),
                                                DoubleArgumentType.getDouble(ctx, "years")))
                                        .then(Commands.argument("targets", EntityArgument.players())
                                                .executes(ctx -> addAge(ctx.getSource(),
                                                        EntityArgument.getPlayers(ctx, "targets"),
                                                        DoubleArgumentType.getDouble(ctx, "years")))))))
                .then(Commands.literal("generation")
                        .then(Commands.literal("set")
                                .then(Commands.argument("value", IntegerArgumentType.integer(1))
                                        .executes(ctx -> setGeneration(ctx.getSource(), List.of(self(ctx)),
                                                IntegerArgumentType.getInteger(ctx, "value")))
                                        .then(Commands.argument("targets", EntityArgument.players())
                                                .executes(ctx -> setGeneration(ctx.getSource(),
                                                        EntityArgument.getPlayers(ctx, "targets"),
                                                        IntegerArgumentType.getInteger(ctx, "value")))))))
                .then(Commands.literal("rebirth")
                        .executes(ctx -> rebirth(ctx.getSource(), List.of(self(ctx))))
                        .then(Commands.argument("targets", EntityArgument.players())
                                .executes(ctx -> rebirth(ctx.getSource(), EntityArgument.getPlayers(ctx, "targets")))))
                .then(Commands.literal("reload")
                        .executes(ctx -> reload(ctx.getSource()))));
    }

    private static ServerPlayer self(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        return ctx.getSource().getPlayerOrException();
    }

    private static int get(CommandSourceStack source, Collection<ServerPlayer> targets) {
        for (ServerPlayer player : targets) {
            player.getCapability(AgeCapability.INSTANCE).ifPresent(age -> source.sendSuccess(() -> Component.literal(
                    player.getGameProfile().getName()
                            + " — " + (int) Math.floor(age.getAgeYears()) + " años"
                            + " · " + age.getStage().name().toLowerCase()
                            + " · Gen " + age.getGeneration()
                            + " · x" + String.format("%.2f", age.getGenerationMultiplier())), false));
        }
        return targets.size();
    }

    private static int setAge(CommandSourceStack source, Collection<ServerPlayer> targets, double years) {
        for (ServerPlayer player : targets) {
            player.getCapability(AgeCapability.INSTANCE).ifPresent(age -> {
                int oldShown = (int) Math.floor(age.getAgeYears());
                LifeStage before = age.getStage();
                age.setAgeYears(years);
                AgeCapability.applyAgeChangeEffects(player, age, oldShown, before);
            });
        }
        source.sendSuccess(() -> Component.literal("Edad fijada a " + (int) Math.floor(years) + " años para " + targets.size() + " jugador(es)."), true);
        return targets.size();
    }

    private static int addAge(CommandSourceStack source, Collection<ServerPlayer> targets, double years) {
        for (ServerPlayer player : targets) {
            player.getCapability(AgeCapability.INSTANCE).ifPresent(age -> {
                int oldShown = (int) Math.floor(age.getAgeYears());
                LifeStage before = age.getStage();
                age.addAgeYears(years);
                AgeCapability.applyAgeChangeEffects(player, age, oldShown, before);
            });
        }
        source.sendSuccess(() -> Component.literal("Sumados " + years + " años a " + targets.size() + " jugador(es)."), true);
        return targets.size();
    }

    private static int setGeneration(CommandSourceStack source, Collection<ServerPlayer> targets, int value) {
        for (ServerPlayer player : targets) {
            player.getCapability(AgeCapability.INSTANCE).ifPresent(age -> age.setGeneration(value));
            AgeBonuses.applyAll(player);
            GenNetwork.syncTo(player);
        }
        source.sendSuccess(() -> Component.literal("Generación fijada a " + value + " para " + targets.size() + " jugador(es)."), true);
        return targets.size();
    }

    private static int reload(CommandSourceStack source) {
        com.yuseix300.dmzgenerations.age.GenerationsConfig.load();
        int count = 0;
        for (ServerPlayer player : source.getServer().getPlayerList().getPlayers()) {
            AgeBonuses.applyAll(player);
            GenNetwork.syncTo(player);
            count++;
        }
        final int synced = count;
        source.sendSuccess(() -> Component.literal("generations.json recargado · re-sincronizados " + synced + " jugador(es)."), true);
        return 1;
    }

    private static int rebirth(CommandSourceStack source, Collection<ServerPlayer> targets) {
        int reborn = 0;
        for (ServerPlayer player : targets) {
            String name = player.getGameProfile().getName();
            Rebirth.Result result = Rebirth.attempt(player);
            switch (result) {
                case SUCCESS -> {
                    reborn++;
                    int gen = player.getCapability(AgeCapability.INSTANCE).resolve().map(AgeData::getGeneration).orElse(1);
                    source.sendSuccess(() -> Component.literal(name + " renació — ahora es Generación " + gen + "."), true);
                }
                case NO_CHARACTER -> source.sendFailure(Component.literal(name + ": no tiene un personaje de DMZ."));
                case NOT_ELDER -> source.sendFailure(Component.literal(name + ": debe ser anciano para renacer."));
                case MAX_GENERATION -> source.sendFailure(Component.literal(name + ": ya está en la generación máxima."));
                case LEVEL_TOO_LOW -> {
                    int req = player.getCapability(AgeCapability.INSTANCE).resolve()
                            .map(a -> AgeSettings.requiredLevelForGeneration(a.getGeneration() + 1)).orElse(0);
                    source.sendFailure(Component.literal(name + ": nivel insuficiente para renacer (necesita " + req + ")."));
                }
            }
        }
        return reborn;
    }
}
