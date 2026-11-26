package com.sepehr.fastbreak.gui;

import com.sepehr.fastbreak.FastBreakConfig;
import com.sepehr.fastbreak.perf.PerfProfile;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.function.BooleanSupplier;

public class PerfControlScreen extends Screen {

    private final Screen parent;

    public PerfControlScreen(Screen parent) {
        super(Text.translatable("screen.sepehrfastbreak.perf.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        FastBreakConfig config = FastBreakConfig.get();

        PerfProfile[] profiles = PerfProfile.values();
        for (int i = 0; i < profiles.length; i++) {
            PerfProfile profile = profiles[i];
            addDrawableChild(ButtonWidget.builder(
                            Text.translatable(profile.labelKey()),
                            button -> profile.apply(this.client.options))
                    .dimensions(centerX - 102 + i * 68, 50, 66, 20)
                    .build());
        }

        addDrawableChild(toggleButton("screen.sepehrfastbreak.perf.autopilot",
                centerX - 102, 82, () -> config.autopilot = !config.autopilot, () -> config.autopilot));
        addDrawableChild(toggleButton("screen.sepehrfastbreak.perf.perfhud",
                centerX + 5, 82, () -> config.perfHud = !config.perfHud, () -> config.perfHud));
        addDrawableChild(toggleButton("screen.sepehrfastbreak.perf.stutteralerts",
                centerX - 102, 106, () -> config.stutterAlerts = !config.stutterAlerts, () -> config.stutterAlerts));

        addDrawableChild(ButtonWidget.builder(
                        Text.translatable("screen.sepehrfastbreak.perf.diagnostics"),
                        button -> {
                            if (this.client != null) {
                                this.client.setScreen(new PerfDiagnosticsScreen(this));
                            }
                        })
                .dimensions(centerX + 5, 106, 97, 20)
                .build());

        addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> close())
                .dimensions(centerX - 100, this.height - 30, 200, 20).build());
    }

    private ButtonWidget toggleButton(String key, int x, int y,
                                      Runnable action,
                                      BooleanSupplier state) {
        return ButtonWidget.builder(Text.translatable(key, onOff(state.getAsBoolean())), button -> {
            action.run();
            button.setMessage(Text.translatable(key, onOff(state.getAsBoolean())));
        }).dimensions(x, y, 97, 20).build();
    }

    private static Text onOff(boolean value) {
        return Text.translatable(value
                ? "screen.sepehrfastbreak.on"
                : "screen.sepehrfastbreak.off");
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 22, 0xFFFFFF);
    }

    @Override
    public void close() {
        FastBreakConfig.save();
        if (this.client != null) {
            this.client.setScreen(parent);
        }
    }
}
