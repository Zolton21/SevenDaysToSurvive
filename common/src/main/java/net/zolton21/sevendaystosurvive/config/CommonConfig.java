package net.zolton21.sevendaystosurvive.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class CommonConfig {
    public static class Server {
        public static final ForgeConfigSpec CONFIG;
        private static final ServerConfigValues VALUES;

        public static ForgeConfigSpec.ConfigValue<List<String>> UNBREAKABLE_BLOCKS_LIST;
        public static ForgeConfigSpec.ConfigValue<Integer> PLAYER_DETECTION_RANGE;
        public static ForgeConfigSpec.ConfigValue<Integer> PLAYER_DETECTION_RANGE_OBLIVION_NIGHT;
        public static ForgeConfigSpec.ConfigValue<Integer> OBLIVION_NIGHT_FREQUENCY;
        public static ForgeConfigSpec.ConfigValue<Boolean> OBLIVION_NIGHT_SYNAPTIC_SEAL_WORKS;
        public static ForgeConfigSpec.ConfigValue<Boolean> ZOMBIES_BUILD_AND_DIG_ONLY_ON_OBLIVION_NIGHT;


        static {
            final Pair<ServerConfigValues, ForgeConfigSpec> specPair =
                    new ForgeConfigSpec.Builder().configure(ServerConfigValues::new);
            VALUES = specPair.getLeft();
            CONFIG = specPair.getRight();
        }

        private static class ServerConfigValues {
            ServerConfigValues(ForgeConfigSpec.Builder builder) {
                builder.comment("General").push("general");

                UNBREAKABLE_BLOCKS_LIST = builder.comment(
                                "List of blocks that zombies cannot break.",
                                "Format: modid:block_name")
                        .define("unbreakable_blocks", List.of("gravestone:gravestone"));

                PLAYER_DETECTION_RANGE = builder.comment(
                                "Defines default player detection range for zombies.",
                                "Higher values make zombies notice players from farther away.")
                        .define("player_detection_range", 60);

                PLAYER_DETECTION_RANGE_OBLIVION_NIGHT = builder.comment(
                                "Defines the increased detection range for zombies during Oblivion Night.",
                                "On Oblivion Night, zombies can detect players from this distance instead of the default range.")
                        .define("player_detection_range_oblivion_night", 120);

                OBLIVION_NIGHT_FREQUENCY = builder.comment(
                                "Defines how often Oblivion Night occurs. Value is in days.",
                                "Example: setting this to 7 means every 7th night is Oblivion Night, where zombies become more aware.")
                        .define("oblivion_night_frequency", 7);

                OBLIVION_NIGHT_SYNAPTIC_SEAL_WORKS = builder.comment(
                                "Defines whether Synaptic Seal works during Oblivion Night.")
                        .define("oblivion_night_synaptic_seal_works", false);

                ZOMBIES_BUILD_AND_DIG_ONLY_ON_OBLIVION_NIGHT = builder.comment(
                                "If true, zombies will only dig and place blocks during Oblivion Night.",
                                "When false, they will retain this behavior at all times (unless suppressed by Synaptic Seals).")
                        .define("zombies_build_and_dig_only_on_oblivion_night", true);

                builder.pop();
            }
        }
    }
}