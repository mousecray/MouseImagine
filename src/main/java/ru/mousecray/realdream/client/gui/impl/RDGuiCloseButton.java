package ru.mousecray.realdream.client.gui.impl;

import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.realdream.client.gui.GuiTexturePack;
import ru.mousecray.realdream.client.gui.RDFontSize;
import ru.mousecray.realdream.client.gui.RDGuiButton;
import ru.mousecray.realdream.client.gui.dim.GuiScaleRules;
import ru.mousecray.realdream.client.gui.dim.GuiScaleType;
import ru.mousecray.realdream.client.gui.dim.GuiShape;
import ru.mousecray.realdream.client.gui.dim.GuiVector;
import ru.mousecray.realdream.client.gui.event.RDGuiMouseClickEvent;
import ru.mousecray.realdream.client.gui.state.GuiButtonActionState;
import ru.mousecray.realdream.client.gui.state.GuiButtonPersistentState;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

@SideOnly(Side.CLIENT)
public class RDGuiCloseButton extends RDGuiButton<RDGuiCloseButton> {
    private final Consumer<RDGuiMouseClickEvent<RDGuiCloseButton>> onClick;

    public RDGuiCloseButton(
            GuiShape elementShape,
            ResourceLocation texture, GuiVector textureSize, GuiShape textureShape,
            RDFontSize fontSize, Consumer<RDGuiMouseClickEvent<RDGuiCloseButton>> onClick) {
        super(
                null, elementShape,
                GuiTexturePack.Builder
                        .create(texture, textureSize, textureShape.pos(), textureShape.size())
                        .addTexture(GuiButtonPersistentState.NORMAL, 0)
                        .addTexture(GuiButtonActionState.HOVER, 1)
                        .addTexture(GuiButtonActionState.PRESSED, 2)
                        .build(),
                SoundEvents.UI_BUTTON_CLICK, fontSize
        );
        this.onClick = onClick;
        setScaleRules(new GuiScaleRules(GuiScaleType.ORIGIN_VERTICAL));
    }

    @Override
    public void onClick(@Nonnull RDGuiMouseClickEvent<RDGuiCloseButton> event) {
        if (onClick != null) onClick.accept(event);
    }
}
