package com.oscarbaltasar.rpchat;


import com.oscarbaltasar.rpchat.Command.RPChatCommand;
import com.oscarbaltasar.rpchat.Config.RPChatConfig;
import com.oscarbaltasar.rpchat.Data.PlayerRPData;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.common.MinecraftForge;

import java.util.HashMap;
import java.util.UUID;

@Mod(RPChatMod.MODID)
public class RPChatMod {
    public static final String MODID = "rpchatmod";
    public static final HashMap<UUID, PlayerRPData> PLAYER_DATA = new HashMap<>();

    public RPChatMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, RPChatConfig.COMMON_CONFIG);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // Initialization logic
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        RPChatCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        UUID uuid = event.getEntity().getUUID();
        PlayerRPData data = new PlayerRPData();
        data.deserializeNBT(event.getEntity().getPersistentData().getCompound("RPChat"));
        PLAYER_DATA.put(uuid, data);
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID uuid = event.getEntity().getUUID();
        PlayerRPData data = PLAYER_DATA.get(uuid);
        if (data != null) {
            event.getEntity().getPersistentData().put("RPChat", data.serializeNBT());
        }
        PLAYER_DATA.remove(uuid);
    }
} 
