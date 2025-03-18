package net.zolton21.sevendaystosurvive.fabric.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.zolton21.sevendaystosurvive.config.IConfig;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;

public class FabricConfig implements IConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "sevendaystosurvive.json");

    private ConfigData configData = new ConfigData();

    private static class ConfigData {
        public List<String> unbreakable_blocks = List.of("gravestone:gravestone");
        public int player_detection_range = 60;
        public int player_detection_range_oblivion_night = 120;
        public int oblivion_night_frequency = 7;
        public boolean oblivion_night_synaptic_seal_works = false;
    }

    @Override
    public void load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                configData = GSON.fromJson(reader, ConfigData.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            save();
        }
    }

    private void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(configData, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> getUnbreakableBlocksList() {
        return configData.unbreakable_blocks;
    }

    @Override
    public int getPlayerDetectionRange() {
        return configData.player_detection_range;
    }

    @Override
    public int getPlayerDetectionRangeOblivionNight() {
        return configData.player_detection_range_oblivion_night;
    }

    @Override
    public int oblivionNightFrequency() {
        return configData.oblivion_night_frequency;
    }

    @Override
    public boolean oblivionNightSynapticSealWorks() {
        return configData.oblivion_night_synaptic_seal_works;
    }
}
