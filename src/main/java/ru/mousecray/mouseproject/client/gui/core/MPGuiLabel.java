/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mouseproject.client.gui.core;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import ru.mousecray.mouseproject.client.gui.core.component.MPGuiRenderHelper;
import ru.mousecray.mouseproject.client.gui.core.component.color.MPGuiColorPack;
import ru.mousecray.mouseproject.client.gui.core.component.lang.MPGuiString;
import ru.mousecray.mouseproject.client.gui.core.component.state.MPGuiElementState;
import ru.mousecray.mouseproject.client.gui.core.dim.IGuiShape;
import ru.mousecray.mouseproject.client.gui.core.dim.MPGuiShape;
import ru.mousecray.mouseproject.client.gui.core.dim.MPMutableGuiShape;
import ru.mousecray.mouseproject.client.gui.core.dim.MPMutableGuiVector;
import ru.mousecray.mouseproject.client.gui.core.event.MPGuiTickEvent;
import ru.mousecray.mouseproject.client.gui.core.misc.MPFontSize;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class MPGuiLabel<T extends MPGuiLabel<T>> extends GuiLabel implements MPGuiElement<T> {
    protected final MPGuiElementCore<T> core;
    private         boolean             centered;

    public MPGuiLabel(MPGuiShape shape) {
        super(
                Minecraft.getMinecraft().fontRenderer, 0,
                (int) shape.x(), (int) shape.y(),
                (int) shape.width(), (int) shape.height(),
                14737632
        );

        core = new MPGuiElementCore<>(shape);
        core.bindEvents(Minecraft.getMinecraft(), self());

        getStateManager().setForbidden(MPGuiElementState.FOCUSED, true);
        setColorPack(MPGuiColorPack.LABEL_SIMPLE());

        getStateManager().setChangeListener(() -> visible = !getStateManager().has(MPGuiElementState.HIDDEN));
    }

    @SuppressWarnings("unchecked") @Override public T self() { return (T) this; }
    @Override public MPGuiElementCore<T> getCore()           { return core; }
    @Override public void setId(int id)                      { this.id = id; }
    @Override public int getId()                             { return id; }

    @Override
    public void setGuiString(MPGuiString guiString) {
        MPGuiElement.super.setGuiString(guiString);
        labels.clear();
        String[] split = guiString.get().split("\n");
        labels.addAll(Arrays.asList(split));
    }

    public void setCentered(boolean centered) { this.centered = centered; }
    public boolean isCentered()               { return centered; }

    @Override
    public void setupShapeToVanilla(IGuiShape result) {
        x = (int) result.x();
        y = (int) result.y();
        width = (int) result.width();
        height = (int) result.height();
    }

    @Override
    public void onDrawText(MPGuiTickEvent<T> event) {
        if (labels.isEmpty()) return;

        FontRenderer fr    = getFontRenderer();
        MPFontSize   fs    = getFontSize();
        int          color = getColorPack().getCalculatedColor(getStateManager());

        float scale    = fs.getScale() * getTextScaleMultiplayer();
        float invScale = 1.0F / scale;

        MPMutableGuiShape calculatedInnerShape = getCalculatedInnerShape();
        float             innerX               = calculatedInnerShape.x();
        float             innerY               = calculatedInnerShape.y();
        float             innerW               = calculatedInnerShape.width();
        float             innerH               = calculatedInnerShape.height();

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
        );

        GlStateManager.pushMatrix();
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GlStateManager.scale(scale, scale, 1.0F);

        MPMutableGuiVector calculatedTextOffsetTemp = getCore().getCalculatedTextOffsetTemp();
        float              totalTextHeight          = labels.size() * fr.FONT_HEIGHT;
        float startY =
                (innerY + innerH / 2f) * invScale - (totalTextHeight / 2f) + calculatedTextOffsetTemp.y() * invScale;

        MPFontSize fontSize = getFontSize();
        for (int i = 0; i < labels.size(); i++) {
            String line  = labels.get(i);
            float  lineY = startY + (i * fr.FONT_HEIGHT);

            if (centered) {
                MPGuiRenderHelper.drawCenteredString(
                        fr, line,
                        (innerX + innerW / 2f) * invScale + calculatedTextOffsetTemp.x() * invScale,
                        lineY,
                        color,
                        fs != MPFontSize.SMALL
                );
            } else {
                MPGuiRenderHelper.drawString(
                        fr, line,
                        innerX * invScale + calculatedTextOffsetTemp.x() * invScale,
                        lineY,
                        color, fontSize != MPFontSize.SMALL
                );
            }
        }

        GlStateManager.popMatrix();
        GlStateManager.disableBlend();
    }

    //Интеграция с vanilla
    @Override
    public boolean mouseHover(Minecraft mc, int mouseX, int mouseY) {
        return MPGuiElement.super.mouseHover(mc, mouseX, mouseY);
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        return MPGuiElement.super.mousePressed(mc, mouseX, mouseY);
    }

    @Override public final int getHoverState(boolean mouseOver)           { return MPGuiElement.super.getHoverState(mouseOver); }
    @Override public void mouseReleased(int mouseX, int mouseY)           { MPGuiElement.super.mouseReleased(mouseX, mouseY); }
    @Override public final void playPressSound(SoundHandler soundHandler) { MPGuiElement.super.playPressSound(soundHandler); }
    @Override public boolean isMouseOver()                                { return MPGuiElement.super.isMouseOver(); }

    @Override
    public void drawLabel(Minecraft mc, int mouseX, int mouseY) {
        dispatchDraw(mc, mouseX, mouseY, mc.getRenderPartialTicks());
    }

    @Override
    protected void drawLabelBackground(Minecraft mc, int mouseX, int mouseY) {
        dispatchDrawBackground(mc, mouseX, mouseY, mc.getRenderPartialTicks());
    }

    @Override
    public GuiLabel setCentered() {
        setCentered(true);
        return this;
    }
}
