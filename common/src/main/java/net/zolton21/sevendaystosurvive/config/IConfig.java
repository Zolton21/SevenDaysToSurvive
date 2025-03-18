package net.zolton21.sevendaystosurvive.config;

import java.util.List;

public interface IConfig {
    void load();
    List<String> getUnbreakableBlocksList();
    int getPlayerDetectionRange();
    int getPlayerDetectionRangeOblivionNight();
    int oblivionNightFrequency();
    boolean oblivionNightSynapticSealWorks();
}
