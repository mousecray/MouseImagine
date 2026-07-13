/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mouseproject.core.client.gui.wallet;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.mouseproject.api.client.gui.component.lang.MGuiString;
import ru.mousecray.mouseproject.api.client.gui.container.MGuiLinearPanel;
import ru.mousecray.mouseproject.api.client.gui.control.MGuiNumberField;
import ru.mousecray.mouseproject.api.client.gui.control.MGuiSimpleSlider;
import ru.mousecray.mouseproject.api.client.gui.dim.GuiShape;
import ru.mousecray.mouseproject.api.client.gui.dim.layout.GuiOrientation;
import ru.mousecray.mouseproject.api.client.gui.dim.layout.GuiPadding;
import ru.mousecray.mouseproject.api.client.gui.dim.layout.GuiScaleRules;
import ru.mousecray.mouseproject.api.client.gui.dim.layout.GuiScaleType;
import ru.mousecray.mouseproject.api.client.gui.event.GuiTextTypedEvent;
import ru.mousecray.mouseproject.api.client.gui.misc.NumberMode;

import java.util.function.Consumer;

@SideOnly(Side.CLIENT)
public class WalletSliderControl extends MGuiLinearPanel {
    private final MGuiNumberField  field;
    private final MGuiSimpleSlider slider;

    private final long maxCoinValue;

    private Consumer<Boolean> validityListener;

    public WalletSliderControl(
            float width,
            float height,
            long maxCoinValue
    ) {
        super(new GuiShape(0, 0, width, height), GuiOrientation.VERTICAL);
        this.maxCoinValue = maxCoinValue;

        field = new MGuiNumberField(
                new GuiShape(0, 0, width, height * 0.65f),
                MGuiString.localizedGuiTag("wallet.text_field.take_put_count"),
                NumberMode.POSITIVE
        );
        field.setOnTextTypedListener(this::onInternalTextTyped);

        field.setScaleRules(new GuiScaleRules(GuiScaleType.PARENT_HORIZONTAL));
        field.setPadding(new GuiPadding(3f, 0, 0, 0));

        float sliderH = height * 0.5f;
        float knobW   = sliderH * (5f / 7f);

        slider = new MGuiSimpleSlider(new GuiShape(0, 0, width, sliderH), knobW, sliderH, 0, 100, GuiOrientation.HORIZONTAL);
        slider.setScaleRules(new GuiScaleRules(GuiScaleType.PARENT_HORIZONTAL));

        slider.setOnSliderChangedListener(event -> {
            long newValue = event.getNewValue() == 0 ? 1 : (long) event.getNewValue() * this.maxCoinValue / 100;
            field.setNumberText(newValue);
        });

        addChild(field);
        addChild(slider);
    }

    private void onInternalTextTyped(GuiTextTypedEvent<MGuiNumberField> event) {
        String newText = event.getNewText();

        if (newText == null || newText.trim().isEmpty()) {
            if (validityListener != null) validityListener.accept(false);
            return;
        }

        if (newText.length() > 19) {
            event.setCancelled(true);
            return;
        }

        try {
            long val = Long.parseLong(newText);
            if (val <= 0) throw new NumberFormatException();

            if (validityListener != null) validityListener.accept(true);

            if (maxCoinValue > 0) {
                int progress = (int) Math.min(100, Math.max(0, (val * 100) / maxCoinValue));
                slider.setValue(progress, false);
            }
        } catch (NumberFormatException e) {
            event.setCancelled(true);
            if (validityListener != null) validityListener.accept(false);
        }
    }

    public WalletSliderControl setOnValidityChanged(Consumer<Boolean> listener) {
        validityListener = listener;
        return this;
    }

    public void setNumberText(long val) { field.setNumberText(val); }
    public long getNumberText()         { return field.getNumberText(); }

    public void addValue(long delta) {
        long current  = field.getNumberText();
        long newValue = current + delta;

        if (newValue <= 0) field.setText("");
        else field.setNumberText(newValue);
    }
}