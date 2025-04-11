
/* 
import com.data_stream.proximitytextchat.Config.ProxTextConfig;
import com.data_stream.proximitytextchat.Events.chatEventHandler;
import com.mojang.logging.LogUtils;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

@Mod("rptextchat")
public class Main {
   public static final String MODID = "rptextchat";
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
   public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

   public Main() {
      IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
      modEventBus.addListener(this::commonSetup);
      BLOCKS.register(modEventBus);
      ITEMS.register(modEventBus);
      MinecraftForge.EVENT_BUS.register(this);
      MinecraftForge.EVENT_BUS.register(chatEventHandler.class);
      ModLoadingContext.get().registerConfig(Type.COMMON, ProxTextConfig.SPEC, MODID+"-common.toml");
   }

   private void commonSetup(FMLCommonSetupEvent event) {
      LOGGER.info("HELLO FROM COMMON SETUP");
   }

   @SubscribeEvent
   public void onServerStarting(ServerStartingEvent event) {
      LOGGER.info("HELLO from server starting");
   }
}
*/

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
