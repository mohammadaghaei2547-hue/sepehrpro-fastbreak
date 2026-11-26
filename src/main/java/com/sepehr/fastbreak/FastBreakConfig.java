package com.sepehr.fastbreak;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FastBreakConfig {

    public static final int MIN_POWER = 1;
    public static final int MAX_POWER = 100;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir()
            .resolve("sepehr-fastbreak.json");

    private static FastBreakConfig instance;

    public boolean enabled = true;
    public boolean showHud = true;
    public int power = 10;

    public static FastBreakConfig get() {
        if (instance == null) {
            instance = new FastBreakConfig();
        }
        return instance;
    }

    public static void load() {
        FastBreakConfig config = new FastBreakConfig();
        try {
            if (Files.exists(PATH)) {
                FastBreakConfig loaded = GSON.fromJson(Files.readString(PATH), FastBreakConfig.class);
                if (loaded != null) {
                    config = loaded;
                }
            }
        } catch (Exception e) {
            SepehrFastBreak.LOGGER.error("Failed to read config, using defaults", e);
        }
        clamp(config);
        instance = config;
    }

    public static void save() {
        try {
            Files.createDirectories(PATH.getParent());
            Files.writeString(PATH, GSON.toJson(get()));
        } catch (IOException e) {
            SepehrFastBreak.LOGGER.error("Failed to save config", e);
        }
    }

    public static void clamp(FastBreakConfig config) {
        if (config.power < MIN_POWER) {
            config.power = MIN_POWER;
        }
        if (config.power > MAX_POWER) {
            config.power = MAX_POWER;
        }
    }
}
