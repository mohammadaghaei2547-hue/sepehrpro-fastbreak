package com.sepehr.fastbreak;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SepehrFastBreak implements ClientModInitializer {

    public static final String MOD_ID = "sepehrfastbreak";
    public static final String VERSION = "2.0.0";
    public static final Logger LOGGER = LoggerFactory.getLogger("SepehrFastBreak");

    private static final BlockPos.Mutable CURSOR = new BlockPos.Mutable();

    private static KeyBinding menuKey;
    private static KeyBinding perfMenuKey;

    @Override
    public void onInitializeClient() {
        FastBreakConfig.load();

        KeyBinding.Category category = KeyBinding.Category.create(Identifier.of(SepehrFastBreak.MOD_ID, "main"));

        menuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.sepehrfastbreak.menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                category
        ));

        perfMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.sepehrfastbreak.perfmenu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_P,
                category
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (menuKey.wasPressed()) {
                client.setScreen(new FastBreakConfigScreen(null));
            }
            while (perfMenuKey.wasPressed()) {
                client.setScreen(new PerfControlScreen(null));
            }
            FastBreakEngine.runTick(client, CURSOR);
            PerfWatchdog.get().tick(client);
        });

        HudElementRegistry.addLast(Identifier.of(SepehrFastBreak.MOD_ID, "status"),
                (context, tickCounter) -> {
                    PerfWatchdog.tracker().onFrame();
                    renderStatusHud(context, tickCounter);
                });

        LOGGER.info("SEPEHR FAST BREAK v{} initialized.", VERSION);
    }

    private static void renderStatusHud(DrawContext context, RenderTickCounter tickCounter) {
        FastBreakConfig config = FastBreakConfig.get();
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden) {
            return;
        }
        int y = 6;

        if (config.enabled && config.showHud) {
            String line = "SEPEHR FAST BREAK: x" + config.power;
            context.drawTextWithShadow(client.textRenderer, line, 6, y, 0xFF55FF55);
            y += 11;
        }

        if (config.perfHud) {
            var tracker = PerfWatchdog.tracker();
            long usedMb = SystemMonitor.usedHeapBytes() / (1024L * 1024L);
            long maxMb = SystemMonitor.maxHeapBytes() / (1024L * 1024L);
            double cpu = SystemMonitor.processCpuLoad();

            context.drawTextWithShadow(client.textRenderer,
                    String.format("FPS: %.0f  Frame: %.1fms  1%%Low: %.0f",
                            tracker.fps(), tracker.avgFrameMs(), tracker.onePercentLowFps()),
                    6, y, 0xFFFFFF);
            y += 11;
            context.drawTextWithShadow(client.textRenderer,
                    String.format("RAM: %d/%d MB  CPU: %s  GC Stutters: %d",
                            usedMb, maxMb,
                            cpu < 0 ? "n/a" : String.format("%.0f%%", cpu * 100.0),
                            tracker.stutters()),
                    6, y, 0xFFFFFF);
            y += 11;

            if (config.stutterAlerts && tracker.recentGcPauseMs() > 0) {
                context.drawTextWithShadow(client.textRenderer,
                        String.format("GC pause: %d ms", tracker.recentGcPauseMs()),
                        6, y, 0xFFFFAA00);
                y += 11;
            }
        }

        if (PerfWatchdog.get().isAlerting()) {
            context.drawTextWithShadow(client.textRenderer,
                    "Auto-Optimizer: Balanced profile applied (FPS low)",
                    6, y, 0xFF55FFFF);
        }
    }
}
