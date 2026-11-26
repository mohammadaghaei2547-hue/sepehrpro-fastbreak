package com.sepehr.fastbreak;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class FastBreakConfigScreen extends Screen {

    private static final int[] PRESETS = {FastBreakConfig.MIN_POWER, 10, 25, FastBreakConfig.MAX_POWER};

    private final Screen parent;

    private PowerSlider powerSlider;

    public FastBreakConfigScreen(Screen parent) {
        super(Text.translatable("screen.sepehrfastbreak.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;

        powerSlider = new PowerSlider(centerX - 100, 50, 200, 20);
        addDrawableChild(powerSlider);

        for (int i = 0; i < PRESETS.length; i++) {
            int preset = PRESETS[i];
            addDrawableChild(ButtonWidget.builder(
                            Text.literal("x" + preset),
                            button -> setPower(preset))
                    .dimensions(centerX - 100 + i * 50, 78, 48, 20)
                    .build());
        }

        FastBreakConfig config = FastBreakConfig.get();

        addDrawableChild(ButtonWidget.builder(toggleText(), button -> {
            config.enabled = !config.enabled;
            button.setMessage(toggleText());
        }).dimensions(centerX - 100, 108, 97, 20).build());

        addDrawableChild(ButtonWidget.builder(hudText(), button -> {
            config.showHud = !config.showHud;
            button.setMessage(hudText());
        }).dimensions(centerX + 3, 108, 97, 20).build());

        addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> close())
                .dimensions(centerX - 100, this.height - 30, 200, 20).build());
    }

    private void setPower(int power) {
        FastBreakConfig config = FastBreakConfig.get();
        config.power = power;
        FastBreakConfig.clamp(config);
        if (powerSlider != null) {
            powerSlider.syncFromConfig();
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.translatable("screen.sepehrfastbreak.hint"),
                this.width / 2, this.height - 46, 0xAAAAAA);
    }

    private Text toggleText() {
        return Text.translatable(
                "screen.sepehrfastbreak.toggle",
                Text.translatable(FastBreakConfig.get().enabled
                        ? "screen.sepehrfastbreak.on"
                        : "screen.sepehrfastbreak.off")
        );
    }

    private Text hudText() {
        return Text.translatable(
                "screen.sepehrfastbreak.hud",
                Text.translatable(FastBreakConfig.get().showHud
                        ? "screen.sepehrfastbreak.on"
                        : "screen.sepehrfastbreak.off")
        );
    }

    @Override
    public void close() {
        FastBreakConfig.save();
        if (this.client != null) {
            this.client.setScreen(parent);
        }
    }

    private static class PowerSlider extends SliderWidget {

        private PowerSlider(int x, int y, int width, int height) {
            super(x, y, width, height, Text.empty(), normalized(FastBreakConfig.get().power));
            updateMessage();
        }

        private static double normalized(int power) {
            return (double) (power - FastBreakConfig.MIN_POWER)
                    / (FastBreakConfig.MAX_POWER - FastBreakConfig.MIN_POWER);
        }

        private int powerValue() {
            return FastBreakConfig.MIN_POWER
                    + (int) Math.round(this.value * (FastBreakConfig.MAX_POWER - FastBreakConfig.MIN_POWER));
        }

        void syncFromConfig() {
            this.value = normalized(FastBreakConfig.get().power);
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            setMessage(Text.translatable("screen.sepehrfastbreak.power", powerValue()));
        }

        @Override
        protected void applyValue() {
            FastBreakConfig config = FastBreakConfig.get();
            config.power = powerValue();
            FastBreakConfig.clamp(config);
        }
    }
}
