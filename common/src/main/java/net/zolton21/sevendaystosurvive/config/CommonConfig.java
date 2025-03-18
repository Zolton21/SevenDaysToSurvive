package net.zolton21.sevendaystosurvive.config;

import java.util.List;

public class CommonConfig {
    public static List<String> UNBREAKABLE_BLOCKS_LIST;
    public static int PLAYER_DETECTION_RANGE;
    public static int PLAYER_DETECTION_RANGE_OBLIVION_NIGHT;
    public static int OBLIVION_NIGHT_FREQUENCY;
    public static boolean OBLIVION_NIGHT_SYNAPTIC_SEAL_WORKS;

    static {
        UNBREAKABLE_BLOCKS_LIST = ConfigManager.CONFIG.getUnbreakableBlocksList();
        PLAYER_DETECTION_RANGE = ConfigManager.CONFIG.getPlayerDetectionRange();
        PLAYER_DETECTION_RANGE_OBLIVION_NIGHT = ConfigManager.CONFIG.getPlayerDetectionRangeOblivionNight();
        OBLIVION_NIGHT_FREQUENCY = ConfigManager.CONFIG.oblivionNightFrequency();
        OBLIVION_NIGHT_SYNAPTIC_SEAL_WORKS = ConfigManager.CONFIG.oblivionNightSynapticSealWorks();
    }
}
