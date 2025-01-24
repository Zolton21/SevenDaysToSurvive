package net.zolton21.sevendaystosurvive.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class Config {
    public static class Server {
        public static final ForgeConfigSpec CONFIG = server();

        public static ForgeConfigSpec.ConfigValue<Integer> ZOMBIE_EXTENDED_AI_COOLDOWN;
        public static ForgeConfigSpec.ConfigValue<List<String>> UNBREAKABLE_BLOCKS_LIST;
        public static ForgeConfigSpec.ConfigValue<Integer> PLAYER_DETECTION_RANGE;

        public static final ForgeConfigSpec server(){
            ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

            builder.comment("General").push("general");
            ZOMBIE_EXTENDED_AI_COOLDOWN = builder.comment("Defines how often zombies can break blocks and execute extended AI. Value is in days.",
                            "Example: setting this to 7 means zombies can break blocks every 7th day, while on other days they cannot")
                    .define("zombie_extended_ai_cooldown", 7);
            UNBREAKABLE_BLOCKS_LIST = builder.comment("List of unbreakable blocks for extended AI:")
                    .define("unbreakable_blocks", List.of("gravestone:gravestone"));

            builder.pop();

            return builder.build();
        }
    }
}
