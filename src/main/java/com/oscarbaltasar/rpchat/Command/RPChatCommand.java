package com.oscarbaltasar.rpchat.Command;

import com.oscarbaltasar.rpchat.RPChatMod;
import com.oscarbaltasar.rpchat.Data.PlayerRPData;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class RPChatCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("ooc")
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
        );

        dispatcher.register(
            Commands.literal("g")
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
        );
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
                .then(Commands.literal("charcolor")
                    .then(Commands.argument("color", StringArgumentType.word())
                        .executes(ctx -> {
                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                            String input = StringArgumentType.getString(ctx, "color").toLowerCase();
                            String aux = input;

                            // Remove leading '#' if present
                            if (input.startsWith("#")) aux = input.substring(1);

                            // Validate hex color code
                            if (!aux.matches("[0-9a-f]{6}")) {
                                ctx.getSource().sendFailure(Component.literal("Invalid color. Use a 6-digit hex code like 00ff00."));
                                return 0;
                            }

                            int rgb = Integer.parseInt(aux, 16);
                            PlayerRPData data = RPChatMod.PLAYER_DATA.get(player.getUUID());
                            data.setCharColor(aux);

                            ctx.getSource().sendSuccess(() ->
                                Component.literal("Character name color set to " + input)
                                    .withStyle(style -> style.withColor(TextColor.fromRgb(rgb))), false);
                            return 1;
                        })
                    )
                )
                .then(Commands.literal("help")
                    .executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayerOrException();
                        UUID uuid = player.getUUID();
                        PlayerRPData data = RPChatMod.PLAYER_DATA.get(uuid);

                        ctx.getSource().sendSuccess(() -> Component.literal("RPChat Help Commands:"), false);
                        ctx.getSource().sendSuccess(() -> Component.literal("/ooc - Toggle in/out of character"), false);
                        ctx.getSource().sendSuccess(() -> Component.literal("/g - Toggle sending to global chat"), false);
                        ctx.getSource().sendSuccess(() -> Component.literal("/rpchat charname <name> - Set your character name"), false);
                        ctx.getSource().sendSuccess(() -> Component.literal("/rpchat globallisten - Toggle listening to global chat"), false);
                        ctx.getSource().sendSuccess(() -> Component.literal("/rpchat charcolor <hexcode> - Set character name color (e.g., 00ff00)"), false);
                        ctx.getSource().sendSuccess(() -> Component.literal("/rpchat help - Displays this help menu"), false);

                        if (data != null) {
                            String chatMode = data.isGlobalChat() ? "Global" : "Local";
                            String listenGlobal = data.isListeningToGlobal() ? "Yes" : "No";
                            String inCharacter = data.isInCharacter() ? "Yes" : "No";
                            String charName = data.getCharacterName() != null ? data.getCharacterName() : "[Not Set]";
                            String charColor = "#" + data.getCharColor();

                            int rgb = Integer.parseInt(data.getCharColor(), 16);

                            ctx.getSource().sendSuccess(() -> Component.literal(""), false); // spacer
                            ctx.getSource().sendSuccess(() -> Component.literal("Your RP Status:"), false);
                            ctx.getSource().sendSuccess(() -> Component.literal("Chat Mode: " + chatMode), false);
                            ctx.getSource().sendSuccess(() -> Component.literal("Listening to Global?: " + listenGlobal), false);
                            ctx.getSource().sendSuccess(() -> Component.literal("In Character?: " + inCharacter), false);
                            ctx.getSource().sendSuccess(() -> Component.literal("Character Name: " + charName), false);
                            ctx.getSource().sendSuccess(() ->
                                Component.literal("Name Color: " + charColor)
                                    .withStyle(style -> style.withColor(TextColor.fromRgb(rgb)))
                            , false);
                        }

                        return 1;
                    })
                )

        );
    }
}
