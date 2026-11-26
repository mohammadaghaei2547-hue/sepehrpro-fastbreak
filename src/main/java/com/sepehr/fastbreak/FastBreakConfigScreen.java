package com.sepehr.fastbreak;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class FastBreakConfigScreen extends Screen {

    private final Screen parent;

    private PowerSlider powerSlider;
    private ButtonWidget toggleButton;

    public FastBreakConfigScreen(Screen parent) {
        super(Text.translatable("screen.sepehrfastbreak.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;

        powerSlider = new PowerSlider(centerX - 100, 60, 200, 20);
        addDrawableChild(powerSlider);

        toggleButton = ButtonWidget.builder(toggleText(), button -> {
            FastBreakConfig.get().enabled = !FastBreakConfig.get().enabled;
            button.setMessage(toggleText());
        }).dimensions(centerX - 100, 90, 200, 20).build();
        addDrawableChild(toggleButton);

        addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> close())
                .dimensions(centerX - 100, this.height - 30, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 30, 0xFFFFFF);
    }

    private Text toggleText() {
        return Text.translatable(
                "screen.sepehrfastbreak.toggle",
                Text.translatable(FastBreakConfig.get().enabled
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
