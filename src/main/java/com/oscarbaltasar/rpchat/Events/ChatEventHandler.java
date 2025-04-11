package com.oscarbaltasar.rpchat.Events;

import com.oscarbaltasar.rpchat.RPChatMod;
import com.oscarbaltasar.rpchat.Config.RPChatConfig;
import com.oscarbaltasar.rpchat.Data.PlayerRPData;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = RPChatMod.MODID)
public class ChatEventHandler {

    @SubscribeEvent
    public static void onServerChat(ServerChatEvent event) {
        ServerPlayer sender = event.getPlayer();
        UUID senderId = sender.getUUID();
        PlayerRPData data = RPChatMod.PLAYER_DATA.get(senderId);
        if (data == null) return;

        boolean isGlobal = data.isGlobalChat();
        boolean isInCharacter = data.isInCharacter();
        String displayName = isInCharacter && data.getCharacterName() != null ? data.getCharacterName() : sender.getName().getString();

        if (isGlobal) {
            event.setCanceled(true);
            for (ServerPlayer player : sender.server.getPlayerList().getPlayers()) {
                PlayerRPData recipientData = RPChatMod.PLAYER_DATA.get(player.getUUID());
                if (recipientData != null && (recipientData.isListeningToGlobal() || recipientData.isGlobalChat())) {
                    player.sendSystemMessage(Component.literal("<" + sender.getName().getString() + "> " + event.getMessage().getString()));
                }
            }
            return;
        }

        // Handle local chat
        event.setCanceled(true);
        List<ServerPlayer> players = sender.server.getPlayerList().getPlayers();
        int shortRange = RPChatConfig.COMMON.shortRange.get();
        int mediumRange = RPChatConfig.COMMON.mediumRange.get();
        int maxRange = RPChatConfig.COMMON.maxRange.get();

        for (ServerPlayer player : players) {
            double distance = player.distanceTo(sender);
            if (distance <= maxRange) {
                String rawMessage = event.getMessage().getString();
                String degraded = degradeText(rawMessage, (float) distance, shortRange, mediumRange, maxRange);
                String senderTag = "<" + displayName + "> ";
                player.sendSystemMessage(Component.literal(senderTag + degraded));
            }
        }
    }

    private static String degradeText(String message, float distance, int shortRange, int mediumRange, int maxRange) {
        if (distance <= shortRange) return message;
        float rangeSpan = maxRange - mediumRange;
        float percent = rangeSpan > 0 ? Math.min((distance - mediumRange) / rangeSpan, 0.5f) : 0f;
        Random rand = new Random();
        StringBuilder degraded = new StringBuilder();
        for (char c : message.toCharArray()) {
            if (Character.isWhitespace(c) || rand.nextFloat() > percent) {
                degraded.append(c);
            } else {
                degraded.append('-');
            }
        }
        return degraded.toString();
    }
}
