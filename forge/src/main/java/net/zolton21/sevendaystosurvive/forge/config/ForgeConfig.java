package net.zolton21.sevendaystosurvive.forge.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.zolton21.sevendaystosurvive.config.IConfig;

import java.util.List;

public class ForgeConfig implements IConfig {
    public static class Server {
        public static final ForgeConfigSpec CONFIG = common();

        public static ForgeConfigSpec.ConfigValue<List<String>> UNBREAKABLE_BLOCKS_LIST;
        public static ForgeConfigSpec.ConfigValue<Integer> PLAYER_DETECTION_RANGE;
        public static ForgeConfigSpec.ConfigValue<Integer> PLAYER_DETECTION_RANGE_OBLIVION_NIGHT;
        public static ForgeConfigSpec.ConfigValue<Integer> OBLIVION_NIGHT_FREQUENCY;
        public static ForgeConfigSpec.ConfigValue<Boolean> OBLIVION_NIGHT_SYNAPTIC_SEAL_WORKS;

        public static final ForgeConfigSpec common(){
            ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

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
                    "Defines whether Synaptic Seal works during Oblivion Night."
            ).define("oblivion_night_synaptic_seal_works", false);

            builder.pop();

            return builder.build();
        }
    }

    @Override
    public void load() {

    }

    @Override
    public List<String> getUnbreakableBlocksList() {
        return Server.UNBREAKABLE_BLOCKS_LIST.get();
    }

    @Override
    public int getPlayerDetectionRange() {
        return Server.PLAYER_DETECTION_RANGE.get();
    }

    @Override
    public int getPlayerDetectionRangeOblivionNight() {
        return Server.PLAYER_DETECTION_RANGE_OBLIVION_NIGHT.get();
    }

    @Override
    public int oblivionNightFrequency() {
        return Server.OBLIVION_NIGHT_FREQUENCY.get();
    }

    @Override
    public boolean oblivionNightSynapticSealWorks() {
        return ForgeConfig.Server.OBLIVION_NIGHT_SYNAPTIC_SEAL_WORKS.get();
    }
}
