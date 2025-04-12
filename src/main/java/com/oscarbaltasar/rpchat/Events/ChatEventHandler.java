package com.oscarbaltasar.rpchat.Events;

import com.oscarbaltasar.rpchat.RPChatMod;
import com.oscarbaltasar.rpchat.Config.RPChatConfig;
import com.oscarbaltasar.rpchat.Data.PlayerRPData;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.HoverEvent.EntityTooltipInfo;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
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
        String rawMessage = event.getMessage().getString();

        if (isGlobal) {
            event.setCanceled(true);
            for (ServerPlayer player : sender.server.getPlayerList().getPlayers()) {
                PlayerRPData recipientData = RPChatMod.PLAYER_DATA.get(player.getUUID());
                if (recipientData != null && (recipientData.isListeningToGlobal() || recipientData.isGlobalChat())) {
                    String hex = data.getCharColor();
                    int rgb = Integer.parseInt(hex, 16);

                    String nameText = sender.getName().getString();
                    Component beforeName = Component.literal("<");
                    Component nameComponent = Component.literal(nameText).withStyle(style -> style.withColor(rgb)
                        .withHoverEvent(new HoverEvent(
                        HoverEvent.Action.SHOW_ENTITY,
                        new EntityTooltipInfo(EntityType.PLAYER, senderId, sender.getName())
                    )));
                    Component afterName = Component.literal("> ");

                    Component finalMessage = Component.empty()
                        .append(beforeName)
                        .append(nameComponent)
                        .append(afterName)
                        .append(event.getMessage());
                    player.sendSystemMessage(finalMessage);
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
                float darknessFactor = getDarknessFactor((float) distance, shortRange, mediumRange, maxRange);
            
                // Handle name color
                TextColor nameColor;
                String hex = data.getCharColor();
                int rgb = Integer.parseInt(hex, 16);
                nameColor = TextColor.fromRgb(applyDarkness(rgb, darknessFactor));
            
                String nameText = (isInCharacter && data.getCharacterName() != null
                                        ? data.getCharacterName()
                                        : sender.getName().getString());
                Component nameComponent = Component.literal(nameText).withStyle(style -> style.withColor(nameColor)
                    .withHoverEvent(new HoverEvent(
                    HoverEvent.Action.SHOW_ENTITY,
                    new EntityTooltipInfo(EntityType.PLAYER, senderId, sender.getName())
                )));
            
                // Message text gets darker too (gray-scale blend)
                int msgColor = applyDarkness(0xBBBBBB, darknessFactor);
                Component beforeName = Component.literal("<").withStyle(style -> style.withColor(TextColor.fromRgb(msgColor)));
                Component afterName = Component.literal("> ").withStyle(style -> style.withColor(TextColor.fromRgb(msgColor)));
                Component messageComponent = Component.literal(degradeText(rawMessage, (float) distance, shortRange, mediumRange, maxRange))
                    .withStyle(style -> style.withColor(TextColor.fromRgb(msgColor)));
            
                Component finalMessage = Component.empty()
                    .append(beforeName)
                    .append(nameComponent)
                    .append(afterName)
                    .append(messageComponent);
                
                player.sendSystemMessage(finalMessage);
            }
            
        }
    }

    private static String degradeText(String message, float distance, int shortRange, int mediumRange, int maxRange) {
        if (distance <= shortRange) return message;
        float rangeSpan = maxRange - mediumRange;
        float percent = rangeSpan > 0 ? Math.min(((distance - mediumRange) / rangeSpan) / 2, 0.5f) : 0f;
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

    private static float getDarknessFactor(float distance, int shortRange, int mediumRange, int maxRange) {
        if (distance <= shortRange) return 0f;
        if (distance >= maxRange) return 1f;
        float rangeSpan = maxRange - mediumRange;
        /* Formula is: If close than medium range: 0.5f, otherwise do (((dx-mr)/mtoh) / 2) + 0.5f. Unless the configuration is extremely wrong this should always be equal or less than 1.0f */
        return distance <= mediumRange ? 0.5f : Math.min((((distance - mediumRange) / rangeSpan) / 2) + 0.5f, 1.0f);
    }
    
    private static int applyDarkness(int rgb, float darknessFactor) {
        int r = (int) (((rgb >> 16) & 0xFF) * (1f - darknessFactor));
        int g = (int) (((rgb >> 8) & 0xFF) * (1f - darknessFactor));
        int b = (int) ((rgb & 0xFF) * (1f - darknessFactor));
        return (r << 16) | (g << 8) | b;
    }
    
}
