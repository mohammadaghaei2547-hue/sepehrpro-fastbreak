package com.sepehr.fastbreak.perf;

import net.minecraft.util.Util;

public final class FrameTimeTracker {

    public static final int SAMPLES = 240;
    private static final double STUTTER_MS = 50.0;

    private final double[] frameTimesMs = new double[SAMPLES];
    private long lastFrameNano = -1L;
    private long stutterCount;
    private long lastGcCount = -1L;
    private long lastGcTimeMs = -1L;
    private long recentGcPauseMs;

    public void onFrame() {
        long now = Util.getMeasuringTimeNano();
        if (lastFrameNano > 0L) {
            double ms = (now - lastFrameNano) / 1_000_000.0;
            frameTimesMs[head()] = Math.min(ms, 5_000.0);
            advance();
            if (ms > STUTTER_MS) {
                stutterCount++;
            }
        }
        lastFrameNano = now;
        pollGc();
    }

    private int index;
    private int filled;

    private int head() {
        return index;
    }

    private void advance() {
        index = (index + 1) % SAMPLES;
        if (filled < SAMPLES) {
            filled++;
        }
    }

    public double avgFrameMs() {
        if (filled == 0) {
            return 0.0;
        }
        double sum = 0.0;
        for (int i = 0; i < filled; i++) {
            sum += frameTimesMs[i];
        }
        return sum / filled;
    }

    public double fps() {
        double avg = avgFrameMs();
        return avg <= 0.0 ? 0.0 : 1000.0 / avg;
    }

    public double worstFrameMs() {
        double worst = 0.0;
        for (int i = 0; i < filled; i++) {
            if (frameTimesMs[i] > worst) {
                worst = frameTimesMs[i];
            }
        }
        return worst;
    }

    public double onePercentLowFps() {
        if (filled == 0) {
            return 0.0;
        }
        double[] sorted = new double[filled];
        System.arraycopy(frameTimesMs, 0, sorted, 0, filled);
        java.util.Arrays.sort(sorted);
        int count = Math.max(1, filled / 100);
        double sum = 0.0;
        for (int i = 0; i < count; i++) {
            sum += sorted[filled - 1 - i];
        }
        double avgWorst = sum / count;
        return avgWorst <= 0.0 ? 0.0 : 1000.0 / avgWorst;
    }

    public long stutters() {
        return stutterCount;
    }

    public long recentGcPauseMs() {
        return recentGcPauseMs;
    }

    private void pollGc() {
        long count = SystemMonitor.totalGcCount();
        long time = SystemMonitor.totalGcTimeMs();
        if (lastGcCount >= 0 && count > lastGcCount) {
            long delta = Math.max(0L, time - Math.max(lastGcTimeMs, 0L));
            if (delta > 0) {
                recentGcPauseMs = delta;
            }
        }
        lastGcCount = count;
        lastGcTimeMs = time;
    }
}
