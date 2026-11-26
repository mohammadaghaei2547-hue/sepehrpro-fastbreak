package com.sepehr.fastbreak;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SepehrFastBreak implements ClientModInitializer {

    public static final String MOD_ID = "sepehrfastbreak";
    public static final Logger LOGGER = LoggerFactory.getLogger("SepehrFastBreak");

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

        ClientTickEvents.END_CLIENT_TICK.register(SepehrFastBreak::onEndClientTick);

        LOGGER.info("SEPEHR FAST BREAK initialized. Default power: {}", FastBreakConfig.get().power);
    }

    private static void onEndClientTick(MinecraftClient client) {
        while (menuKey.wasPressed()) {
            client.setScreen(new FastBreakConfigScreen(null));
        }
        tickFastBreak(client);
    }

    private static void tickFastBreak(MinecraftClient client) {
        FastBreakConfig config = FastBreakConfig.get();
        if (!config.enabled || config.power <= FastBreakConfig.MIN_POWER) {
            return;
        }
        if (client.player == null || client.world == null || client.interactionManager == null) {
            return;
        }
        if (client.currentScreen != null) {
            return;
        }
        if (!client.options.attackKey.isPressed()) {
            return;
        }
        if (client.crosshairTarget == null || client.crosshairTarget.getType() != HitResult.Type.BLOCK) {
            return;
        }

        BlockHitResult hit = (BlockHitResult) client.crosshairTarget;
        int extraHits = Math.min(config.power, FastBreakConfig.MAX_POWER) - 1;

        for (int i = 0; i < extraHits; i++) {
            client.interactionManager.updateBlockBreakingProgress(hit.getBlockPos(), hit.getSide());
        }
    }
}
