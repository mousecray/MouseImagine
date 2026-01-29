package ru.mousecray.realdream.client.gui.impl;

import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.realdream.client.gui.GuiTexturePack;
import ru.mousecray.realdream.client.gui.RDFontSize;
import ru.mousecray.realdream.client.gui.RDGuiButton;
import ru.mousecray.realdream.client.gui.dim.GuiShape;
import ru.mousecray.realdream.client.gui.dim.GuiVector;
import ru.mousecray.realdream.client.gui.event.RDGuiMouseClickEvent;
import ru.mousecray.realdream.client.gui.state.GuiButtonActionState;
import ru.mousecray.realdream.client.gui.state.GuiButtonPersistentState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

@SideOnly(Side.CLIENT)
public class RDGuiSelectedButton<T extends RDGuiSelectedButton<T>> extends RDGuiButton<T> {
    private final Consumer<RDGuiMouseClickEvent<T>> onClick;

    public RDGuiSelectedButton(
            @Nullable String text,
            GuiShape elementShape,
            ResourceLocation texture, GuiVector textureSize, GuiShape textureShape,
            RDFontSize fontSize, Consumer<RDGuiMouseClickEvent<T>> onClick) {
        super(
                text, elementShape,
                GuiTexturePack.Builder
                        .create(texture, textureSize, textureShape.pos(), textureShape.size())
                        .addTexture(GuiButtonPersistentState.NORMAL, 0)
                        .addTexture(GuiButtonActionState.HOVER, 1)
                        .addTexture(GuiButtonActionState.PRESSED, 2)
                        .addTexture(GuiButtonPersistentState.NORMAL.combine(GuiButtonActionState.HOVER), 1)
                        .addTexture(GuiButtonPersistentState.NORMAL.combine(GuiButtonActionState.PRESSED), 2)
                        .addTexture(GuiButtonPersistentState.SELECTED, 3)
                        .addTexture(GuiButtonPersistentState.SELECTED.combine(GuiButtonActionState.HOVER), 4)
                        .addTexture(GuiButtonPersistentState.SELECTED.combine(GuiButtonActionState.PRESSED), 5)
                        .build(),
                SoundEvents.UI_BUTTON_CLICK, fontSize
        );
        this.onClick = onClick;
    }

    @Override
    public void onClick(@Nonnull RDGuiMouseClickEvent<T> event) {
        applyState(
                getPersistentState() == GuiButtonPersistentState.SELECTED
                        ? GuiButtonPersistentState.NORMAL : GuiButtonPersistentState.SELECTED
        );
        if (onClick != null) onClick.accept(event);
    }
}
