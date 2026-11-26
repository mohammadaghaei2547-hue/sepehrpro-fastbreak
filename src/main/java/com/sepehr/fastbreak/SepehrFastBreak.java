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
    public static final String VERSION = "1.1.0";
    public static final Logger LOGGER = LoggerFactory.getLogger("SepehrFastBreak");

    private static final BlockPos.Mutable CURSOR = new BlockPos.Mutable();

    private static KeyBinding menuKey;

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

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (menuKey.wasPressed()) {
                client.setScreen(new FastBreakConfigScreen(null));
            }
            FastBreakEngine.runTick(client, CURSOR);
        });

        HudElementRegistry.addLast(Identifier.of(SepehrFastBreak.MOD_ID, "status"),
                (context, tickCounter) -> renderStatusHud(context));

        LOGGER.info("SEPEHR FAST BREAK v{} initialized. Default power: {}",
                VERSION, FastBreakConfig.get().power);
    }

    private static void renderStatusHud(DrawContext context) {
        FastBreakConfig config = FastBreakConfig.get();
        if (!config.showHud || !config.enabled) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden) {
            return;
        }
        String line = "SEPEHR FAST BREAK: x" + config.power;
        context.drawTextWithShadow(client.textRenderer, line, 6, 6, 0xFF55FF55);
    }
}
