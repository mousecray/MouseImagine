package ru.mousecray.realdream.client.gui.impl;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;
import ru.mousecray.realdream.client.gui.RDGuiTextField;
import ru.mousecray.realdream.client.gui.dim.GuiShape;
import ru.mousecray.realdream.client.gui.dim.GuiVector;
import ru.mousecray.realdream.client.gui.event.RDGuiEvent;
import ru.mousecray.realdream.client.gui.event.RDGuiTextTypedEvent;
import ru.mousecray.realdream.client.gui.misc.RDFontSize;
import ru.mousecray.realdream.client.gui.misc.lang.RDGuiString;
import ru.mousecray.realdream.client.gui.misc.texture.RDGuiTexturePack;
import ru.mousecray.realdream.client.gui.state.GuiButtonActionState;
import ru.mousecray.realdream.client.gui.state.GuiButtonPersistentState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

@SideOnly(Side.CLIENT)
public class RDGuiNumberField extends RDGuiTextField<RDGuiNumberField> {
    private final Consumer<RDGuiTextTypedEvent<RDGuiNumberField>> onTextTyped;

    public RDGuiNumberField(
            FontRenderer fontRenderer, @Nullable RDGuiString placeholder,
            GuiShape elementShape,
            ResourceLocation texture, GuiVector textureSize, GuiShape textureShape,
            RDFontSize fontSize, Consumer<RDGuiTextTypedEvent<RDGuiNumberField>> onTextTyped
    ) {
        super(fontRenderer, placeholder == null ? "" : placeholder.get(), "", elementShape,
                RDGuiTexturePack.Builder
                        .create(texture, textureSize, textureShape.pos(), textureShape.size())
                        .addTexture(GuiButtonPersistentState.NORMAL, 0)
                        .addTexture(GuiButtonActionState.HOVER, 0)
                        .addTexture(GuiButtonActionState.PRESSED, 0)
                        .build()
                , null, fontSize);
        this.onTextTyped = onTextTyped;
        if (placeholder != null) setPlaceholder(placeholder);
    }

    public RDGuiNumberField(
            FontRenderer fontRenderer, @Nullable String placeholder,
            GuiShape elementShape,
            ResourceLocation texture, GuiVector textureSize, GuiShape textureShape,
            RDFontSize fontSize, Consumer<RDGuiTextTypedEvent<RDGuiNumberField>> onTextTyped
    ) {
        super(fontRenderer, placeholder, "", elementShape,
                RDGuiTexturePack.Builder
                        .create(texture, textureSize, textureShape.pos(), textureShape.size())
                        .addTexture(GuiButtonPersistentState.NORMAL, 0)
                        .addTexture(GuiButtonActionState.HOVER, 0)
                        .addTexture(GuiButtonActionState.PRESSED, 0)
                        .build()
                , null, fontSize);
        this.onTextTyped = onTextTyped;
    }

    @Override
    protected void onAnyEventFire(@Nonnull RDGuiEvent<RDGuiNumberField> event) {
        if (event instanceof RDGuiTextTypedEvent) {
            RDGuiTextTypedEvent<RDGuiNumberField> e       = (RDGuiTextTypedEvent<RDGuiNumberField>) event;
            String                                newText = e.getNewText();

            if (newText.isEmpty()) {
                if (onTextTyped != null) onTextTyped.accept(e);
                return;
            }

            if (!StringUtils.isNumeric(newText) || (newText.length() > 1 && newText.startsWith("0"))) {
                e.setCancelled(true);
            } else {
                if (onTextTyped != null) onTextTyped.accept(e);
            }
        }
    }

    public long getNumberText()           { return StringUtils.isEmpty(getText()) ? 0 : Long.parseLong(getText()); }
    public void setNumberText(long value) { setText(String.valueOf(value)); }
}