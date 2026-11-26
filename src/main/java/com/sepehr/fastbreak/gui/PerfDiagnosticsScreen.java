package com.sepehr.fastbreak.gui;

import com.sepehr.fastbreak.perf.PerfWatchdog;
import com.sepehr.fastbreak.perf.SystemMonitor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class PerfDiagnosticsScreen extends Screen {

    private final Screen parent;

    public PerfDiagnosticsScreen(Screen parent) {
        super(Text.translatable("screen.sepehrfastbreak.diag.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> close())
                .dimensions(this.width / 2 - 100, this.height - 30, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        var tracker = PerfWatchdog.tracker();
        long usedMb = SystemMonitor.usedHeapBytes() / (1024L * 1024L);
        long maxMb = SystemMonitor.maxHeapBytes() / (1024L * 1024L);
        double cpu = SystemMonitor.processCpuLoad();
        double heapPercent = maxMb > 0 ? 100.0 * usedMb / maxMb : 0.0;

        List<String> lines = new ArrayList<>();
        lines.add("== Frames ==");
        lines.add(String.format("Average FPS      : %.1f", tracker.fps()));
        lines.add(String.format("1%% Low FPS       : %.1f", tracker.onePercentLowFps()));
        lines.add(String.format("Avg Frame Time   : %.2f ms", tracker.avgFrameMs()));
        lines.add(String.format("Worst Frame Time : %.2f ms", tracker.worstFrameMs()));
        lines.add(String.format("Stutters (>50ms) : %d", tracker.stutters()));
        lines.add("");
        lines.add("== Memory ==");
        lines.add(String.format("Heap Used        : %d MB", usedMb));
        lines.add(String.format("Heap Max         : %d MB", maxMb));
        lines.add(String.format("Heap Usage       : %.1f%%", heapPercent));
        lines.add(String.format("Total GC Count   : %d", SystemMonitor.totalGcCount()));
        lines.add(String.format("Total GC Time    : %d ms", SystemMonitor.totalGcTimeMs()));
        lines.add(String.format("Last GC Pause    : %d ms", tracker.recentGcPauseMs()));
        lines.add("");
        lines.add("== CPU / Threads ==");
        lines.add(String.format("Process CPU Load : %s", cpu < 0 ? "n/a" : String.format("%.1f%%", cpu * 100.0)));
        lines.add(String.format("Live Threads     : %d", SystemMonitor.threadCount()));
        lines.add("");
        lines.add("== GPU ==");
        lines.add("GPU/VRAM stats are not exposed by the Java client; check your NVIDIA overlay.");

        int y = 32;
        for (String line : lines) {
            if (!line.isEmpty()) {
                context.drawTextWithShadow(this.textRenderer, line, 24, y, 0xFFFFFF);
            }
            y += 11;
        }
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(parent);
        }
    }
}
