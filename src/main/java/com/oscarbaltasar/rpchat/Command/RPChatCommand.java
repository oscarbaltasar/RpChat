package com.oscarbaltasar.rpchat.Command;

import com.oscarbaltasar.rpchat.RPChatMod;
import com.oscarbaltasar.rpchat.Data.PlayerRPData;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class RPChatCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("rpchat")
                .then(Commands.literal("charname")
                    .then(Commands.argument("name", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                            UUID uuid = player.getUUID();
                            String name = StringArgumentType.getString(ctx, "name");
                            PlayerRPData data = RPChatMod.PLAYER_DATA.get(uuid);
                            if (data != null) {
                                data.setCharacterName(name);
                                ctx.getSource().sendSuccess(() -> Component.literal("Character name set to: " + name), false);
                            }
                            return 1;
                        })
                    )
                )
                .then(Commands.literal("ooc")
                    .executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayerOrException();
                        UUID uuid = player.getUUID();
                        PlayerRPData data = RPChatMod.PLAYER_DATA.get(uuid);
                        if (data != null) {
                            data.toggleInCharacter();
                            boolean inChar = data.isInCharacter();
                            ctx.getSource().sendSuccess(() -> Component.literal(
                                inChar ? "You are currently IN character" : "You are currently OUT of character"
                            ), false);
                        }
                        return 1;
                    })
                )
                .then(Commands.literal("global")
                    .executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayerOrException();
                        UUID uuid = player.getUUID();
                        PlayerRPData data = RPChatMod.PLAYER_DATA.get(uuid);
                        if (data != null) {
                            data.toggleGlobalChat();
                            boolean global = data.isGlobalChat();
                            ctx.getSource().sendSuccess(() -> Component.literal(
                                global ? "You are currently in global chat" : "You are currently in local chat"
                            ), false);
                        }
                        return 1;
                    })
                )
                .then(Commands.literal("globallisten")
                    .executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayerOrException();
                        UUID uuid = player.getUUID();
                        PlayerRPData data = RPChatMod.PLAYER_DATA.get(uuid);
                        if (data != null && !data.isGlobalChat()) {
                            data.toggleListeningToGlobal();
                            boolean listen = data.isListeningToGlobal();
                            ctx.getSource().sendSuccess(() -> Component.literal(
                                listen ? "You are currently listening to global chat" : "You stopped listening to global chat"
                            ), false);
                        } else if (data != null) {
                            ctx.getSource().sendSuccess(() -> Component.literal("You are in global chat and cannot stop listening."), false);
                        }
                        return 1;
                    })
                )
        );
    }
}
