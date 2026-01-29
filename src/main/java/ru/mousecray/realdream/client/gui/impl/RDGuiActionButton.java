package ru.mousecray.realdream.client.gui.impl;

import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.realdream.client.gui.RDGuiButton;
import ru.mousecray.realdream.client.gui.dim.GuiShape;
import ru.mousecray.realdream.client.gui.dim.GuiVector;
import ru.mousecray.realdream.client.gui.event.RDGuiMouseClickEvent;
import ru.mousecray.realdream.client.gui.misc.RDFontSize;
import ru.mousecray.realdream.client.gui.misc.lang.RDGuiString;
import ru.mousecray.realdream.client.gui.misc.texture.RDGuiTexturePack;
import ru.mousecray.realdream.client.gui.state.GuiButtonActionState;
import ru.mousecray.realdream.client.gui.state.GuiButtonPersistentState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

@SideOnly(Side.CLIENT)
public class RDGuiActionButton extends RDGuiButton<RDGuiActionButton> {
    private final Consumer<RDGuiMouseClickEvent<RDGuiActionButton>> onClick;

    public RDGuiActionButton(
            @Nullable RDGuiString text,
            GuiShape elementShape,
            ResourceLocation texture, GuiVector textureSize, GuiShape textureShape,
            RDFontSize fontSize, Consumer<RDGuiMouseClickEvent<RDGuiActionButton>> onClick) {
        super(
                text == null ? "" : text.get(), elementShape,
                RDGuiTexturePack.Builder
                        .create(texture, textureSize, textureShape.pos(), textureShape.size())
                        .addTexture(GuiButtonPersistentState.DISABLED, 0)
                        .addTexture(GuiButtonPersistentState.NORMAL, 1)
                        .addTexture(GuiButtonActionState.HOVER, 2)
                        .addTexture(GuiButtonActionState.PRESSED, 3)
                        .addTexture(GuiButtonPersistentState.FAIL, 4)
                        .build(),
                SoundEvents.UI_BUTTON_CLICK, fontSize
        );
        this.onClick = onClick;
        if (text != null) setGuiString(text);
    }

    public RDGuiActionButton(
            @Nullable String text,
            GuiShape elementShape,
            ResourceLocation texture, GuiVector textureSize, GuiShape textureShape,
            RDFontSize fontSize, Consumer<RDGuiMouseClickEvent<RDGuiActionButton>> onClick) {
        super(
                text, elementShape,
                RDGuiTexturePack.Builder
                        .create(texture, textureSize, textureShape.pos(), textureShape.size())
                        .addTexture(GuiButtonPersistentState.DISABLED, 0)
                        .addTexture(GuiButtonPersistentState.NORMAL, 1)
                        .addTexture(GuiButtonActionState.HOVER, 2)
                        .addTexture(GuiButtonActionState.PRESSED, 3)
                        .addTexture(GuiButtonPersistentState.FAIL, 4)
                        .build(),
                SoundEvents.UI_BUTTON_CLICK, fontSize
        );
        this.onClick = onClick;
    }

    @Override
    public void onClick(@Nonnull RDGuiMouseClickEvent<RDGuiActionButton> event) {
        if (onClick != null) onClick.accept(event);
    }
}
