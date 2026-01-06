package ru.mousecray.realdream.client.gui.impl;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import ru.mousecray.realdream.client.gui.*;
import ru.mousecray.realdream.client.gui.dim.GuiShape;
import ru.mousecray.realdream.client.gui.event.RDGuiMouseClickEvent;
import ru.mousecray.realdream.client.gui.event.RDGuiTickEvent;
import ru.mousecray.realdream.client.gui.state.GuiButtonActionState;
import ru.mousecray.realdream.client.gui.state.GuiButtonPersistentState;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

@SideOnly(Side.CLIENT)
public class RDGuiCheckButton extends RDGuiButton<RDGuiCheckButton> {
    private final Consumer<RDGuiMouseClickEvent<RDGuiCheckButton>> onClick;

    public RDGuiCheckButton(
            String text,
            FontRenderer fontRenderer,
            GuiShape elementShape,
            ResourceLocation texture, GuiVector textureSize, GuiShape textureShape,
            RDFontSize fontSize, Consumer<RDGuiMouseClickEvent<RDGuiCheckButton>> onClick) {
        super(
                text,
                elementShape.grow(-fontRenderer.getStringWidth(text) - 1, 0, 0, 0),
                elementShape,
                GuiTexturePack.Builder
                        .create(texture, textureSize, textureShape.getPos(), textureShape.getSize())
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
        colorContainer = StateColorContainer.Builder
                .create(14737632)
                .addState(GuiButtonPersistentState.DISABLED, 10526880)
                .addState(GuiButtonPersistentState.NORMAL, 14737632)
                .addState(GuiButtonActionState.HOVER, 15592941)
                .addState(GuiButtonActionState.PRESSED, 13948116)
                .addState(GuiButtonPersistentState.NORMAL.combine(GuiButtonActionState.HOVER), 15592941)
                .addState(GuiButtonPersistentState.NORMAL.combine(GuiButtonActionState.PRESSED), 13948116)
                .addState(GuiButtonPersistentState.SELECTED, 14737632)
                .addState(GuiButtonPersistentState.SELECTED.combine(GuiButtonActionState.HOVER), 15592941)
                .addState(GuiButtonPersistentState.SELECTED.combine(GuiButtonActionState.PRESSED), 13948116)
                .build();
    }

    @Override
    protected void onClick(@Nonnull RDGuiMouseClickEvent<RDGuiCheckButton> event) {
        applyState(
                getPersistentState() == GuiButtonPersistentState.SELECTED
                        ? GuiButtonPersistentState.NORMAL : GuiButtonPersistentState.SELECTED
        );
        if (onClick != null) onClick.accept(event);
    }

    @Override
    protected void drawButtonTextLayer(@Nonnull RDGuiTickEvent<RDGuiCheckButton> event) {
        if (displayString != null) {
            FontRenderer fontrenderer = event.getMc().fontRenderer;
            int          color;
            if (packedFGColour != 0) color = packedFGColour;
            else if (actionState != null) {
                color = colorContainer.getColor(actionState.combine(persistentState));
                if (color == -1) {
                    color = colorContainer.getColor(actionState);
                    if (color == -1) color = colorContainer.getColor(persistentState);
                }
            } else color = colorContainer.getColor(persistentState);

            if (color == -1) color = colorContainer.getDefaultColor();

            float scale        = fontSize.getScale() * textScaleMultiplayer;
            float inverseScale = 1.0F / scale;

            GlStateManager.pushMatrix();
            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

            GlStateManager.scale(scale, scale, 1.0F);
            GuiRenderHelper.drawString(
                    fontrenderer, displayString,
                    elementShape.getX() * inverseScale + drawOffsetX * inverseScale + textOffsetX * inverseScale,
                    elementShape.getY() * inverseScale + drawShape.getHeight() * inverseScale / 2f - (fontrenderer.FONT_HEIGHT) / 2f + drawOffsetY * inverseScale + textOffsetY * inverseScale,
                    color, fontSize != RDFontSize.SMALL
            );
            GlStateManager.popMatrix();
        }
    }
}
