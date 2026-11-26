package com.sepehr.fastbreak.perf;

import com.sepehr.fastbreak.FastBreakConfig;
import net.minecraft.client.MinecraftClient;

public final class PerfWatchdog {

    private static final int CHECK_INTERVAL_TICKS = 200;
    private static final double FPS_THRESHOLD = 40.0;
    private static final int ALERT_DURATION_TICKS = 400;

    private int tickCounter;
    private int alertTicks;

    private PerfWatchdog() {
    }

    private static final PerfWatchdog INSTANCE = new PerfWatchdog();

    public static PerfWatchdog get() {
        return INSTANCE;
    }

    public void tick(MinecraftClient client) {
        if (alertTicks > 0) {
            alertTicks--;
        }
        tickCounter++;
        if (tickCounter < CHECK_INTERVAL_TICKS) {
            return;
        }
        tickCounter = 0;

        FastBreakConfig config = FastBreakConfig.get();
        if (!config.autopilot || !config.enabled) {
            return;
        }
        if (client.world == null || client.player == null) {
            return;
        }
        if (client.currentScreen != null) {
            return;
        }
        double fps = TRACKER.fps();
        if (fps > 0.0 && fps < FPS_THRESHOLD && !config.autopilotApplied) {
            PerfProfile.BALANCED.apply(client.options);
            config.autopilotApplied = true;
            alertTicks = ALERT_DURATION_TICKS;
        }
    }

    private static final FrameTimeTracker TRACKER = new FrameTimeTracker();

    public static FrameTimeTracker tracker() {
        return TRACKER;
    }

    public boolean isAlerting() {
        return alertTicks > 0;
    }
}
