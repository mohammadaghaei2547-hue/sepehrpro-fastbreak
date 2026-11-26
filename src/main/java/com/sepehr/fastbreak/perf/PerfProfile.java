package com.sepehr.fastbreak.perf;

import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.GameOptions;
import net.minecraft.particle.ParticlesMode;

public enum PerfProfile {

    BATTERY("screen.sepehrfastbreak.profile.battery") {
        @Override
        public void apply(GameOptions options) {
            options.getViewDistance().setValue(6);
            options.getSimulationDistance().setValue(6);
            options.getParticles().setValue(ParticlesMode.MINIMAL);
            options.getEntityDistanceScaling().setValue(0.5);
            options.getAo().setValue(false);
            options.getCloudRenderMode().setValue(CloudRenderMode.OFF);
            options.getEnableVsync().setValue(true);
            options.getMaxFps().setValue(60);
            options.sendClientSettings();
        }
    },

    BALANCED("screen.sepehrfastbreak.profile.balanced") {
        @Override
        public void apply(GameOptions options) {
            options.getViewDistance().setValue(10);
            options.getSimulationDistance().setValue(8);
            options.getParticles().setValue(ParticlesMode.DECREASED);
            options.getEntityDistanceScaling().setValue(0.75);
            options.getAo().setValue(true);
            options.getCloudRenderMode().setValue(CloudRenderMode.FAST);
            options.getEnableVsync().setValue(true);
            options.getMaxFps().setValue(90);
            options.sendClientSettings();
        }
    },

    MAX_FPS("screen.sepehrfastbreak.profile.maxfps") {
        @Override
        public void apply(GameOptions options) {
            options.getViewDistance().setValue(12);
            options.getSimulationDistance().setValue(10);
            options.getParticles().setValue(ParticlesMode.MINIMAL);
            options.getEntityDistanceScaling().setValue(0.5);
            options.getAo().setValue(false);
            options.getCloudRenderMode().setValue(CloudRenderMode.OFF);
            options.getEnableVsync().setValue(false);
            options.getMaxFps().setValue(260);
            options.sendClientSettings();
        }
    };

    private final String labelKey;

    PerfProfile(String labelKey) {
        this.labelKey = labelKey;
    }

    public String labelKey() {
        return labelKey;
    }

    public abstract void apply(GameOptions options);
}
