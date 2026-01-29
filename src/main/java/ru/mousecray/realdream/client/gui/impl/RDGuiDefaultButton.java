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
public class RDGuiDefaultButton extends RDGuiButton<RDGuiDefaultButton> {
    private final Consumer<RDGuiMouseClickEvent<RDGuiDefaultButton>> onClick;

    public RDGuiDefaultButton(
            @Nullable String text,
            GuiShape elementShape,
            ResourceLocation texture, GuiVector textureSize, GuiShape textureShape,
            RDFontSize fontSize, Consumer<RDGuiMouseClickEvent<RDGuiDefaultButton>> onClick) {
        super(
                text, elementShape,
                GuiTexturePack.Builder
                        .create(texture, textureSize, textureShape.pos(), textureShape.size())
                        .addTexture(GuiButtonPersistentState.NORMAL, 0)
                        .addTexture(GuiButtonActionState.HOVER, 1)
                        .addTexture(GuiButtonActionState.PRESSED, 2)
                        .build(),
                SoundEvents.UI_BUTTON_CLICK, fontSize
        );
        this.onClick = onClick;
    }

    @Override
    public void onClick(@Nonnull RDGuiMouseClickEvent<RDGuiDefaultButton> event) {
        if (onClick != null) onClick.accept(event);
    }
}
