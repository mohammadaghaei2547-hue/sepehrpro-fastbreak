package com.sepehr.fastbreak;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

public final class FastBreakEngine {

    private FastBreakEngine() {
    }

    public static boolean isMining(MinecraftClient client) {
        FastBreakConfig config = FastBreakConfig.get();
        if (!config.enabled || config.power <= FastBreakConfig.MIN_POWER) {
            return false;
        }
        if (client.player == null || client.world == null || client.interactionManager == null) {
            return false;
        }
        if (client.currentScreen != null) {
            return false;
        }
        if (!client.options.attackKey.isPressed()) {
            return false;
        }
        return client.crosshairTarget != null
                && client.crosshairTarget.getType() == HitResult.Type.BLOCK;
    }

    public static void runTick(MinecraftClient client, BlockPos.Mutable cursor) {
        if (!isMining(client)) {
            return;
        }

        BlockHitResult hit = (BlockHitResult) client.crosshairTarget;
        BlockPos pos = hit.getBlockPos();
        cursor.set(pos);

        BlockState state = client.world.getBlockState(cursor);
        if (state.isAir()) {
            return;
        }

        int power = FastBreakConfig.get().power;
        int extraHits = Math.min(power, FastBreakConfig.MAX_POWER) - 1;

        var manager = client.interactionManager;

        for (int i = 0; i < extraHits; i++) {
            if (!manager.updateBlockBreakingProgress(pos, hit.getSide())) {
                manager.attackBlock(pos, hit.getSide());
            }
        }
    }
}
