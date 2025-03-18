package net.zolton21.sevendaystosurvive.config;

import dev.architectury.platform.Platform;

public class ConfigManager {
    public static final IConfig CONFIG;

    static {
        if(Platform.isForge()){
            CONFIG = loadForgeConfig();
        }else{
            CONFIG = loadFabricConfig();
        }
    }

    private static IConfig loadForgeConfig() {
        try {
            Class<?> forgeConfigClass = Class.forName("net.zolton21.sevendaystosurvive.forge.config.ForgeConfig");
            return (IConfig) forgeConfigClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load Forge config", e);
        }
    }

    public static IConfig loadFabricConfig(){
        try {
            Class<?> fabricConfigClass = Class.forName("net.zolton21.sevendaystosurvive.fabric.config.FabricConfig");
            return (IConfig) fabricConfigClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load Fabric config", e);
        }
    }

    public static void load(){
        CONFIG.load();
    }
}
