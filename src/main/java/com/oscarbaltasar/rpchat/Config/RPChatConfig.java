package com.oscarbaltasar.rpchat.Config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber
public class RPChatConfig {

    public static final ForgeConfigSpec COMMON_CONFIG;
    public static final Common COMMON;

    static {
        Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON = specPair.getLeft();
        COMMON_CONFIG = specPair.getRight();
    }

    public static class Common {
        public final ForgeConfigSpec.IntValue shortRange;
        public final ForgeConfigSpec.IntValue mediumRange;
        public final ForgeConfigSpec.IntValue maxRange;

        public Common(ForgeConfigSpec.Builder builder) {
            builder.push("Local Chat Ranges");

            shortRange = builder
                .comment("Distance for full clarity local chat (default: 30)")
                .defineInRange("shortRange", 30, 1, 1000);

            mediumRange = builder
                .comment("Distance for partial degradation to start (default: 50)")
                .defineInRange("mediumRange", 50, 1, 1000);

            maxRange = builder
                .comment("Maximum range for local chat before cutoff (default: 100)")
                .defineInRange("maxRange", 100, 1, 1000);

            builder.pop();
        }
    }
}
