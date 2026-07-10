/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mouseproject.client.gui.core;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import ru.mousecray.mouseproject.MouseProject;
import ru.mousecray.mouseproject.client.gui.core.component.MPGuiRenderHelper;
import ru.mousecray.mouseproject.client.gui.core.component.color.MPGuiColorPack;
import ru.mousecray.mouseproject.client.gui.core.component.lang.MPGuiString;
import ru.mousecray.mouseproject.client.gui.core.component.sound.MPGuiSoundPack;
import ru.mousecray.mouseproject.client.gui.core.component.sound.MPSoundSourceType;
import ru.mousecray.mouseproject.client.gui.core.component.state.MPGuiElementState;
import ru.mousecray.mouseproject.client.gui.core.component.state.MPGuiElementStateManager;
import ru.mousecray.mouseproject.client.gui.core.component.texture.MPGuiTexture;
import ru.mousecray.mouseproject.client.gui.core.component.texture.MPGuiTexturePack;
import ru.mousecray.mouseproject.client.gui.core.dim.*;
import ru.mousecray.mouseproject.client.gui.core.dim.layout.MPGuiPadding;
import ru.mousecray.mouseproject.client.gui.core.event.*;
import ru.mousecray.mouseproject.client.gui.core.misc.MPFontSize;
import ru.mousecray.mouseproject.client.gui.core.misc.MPMoveDirection;
import ru.mousecray.mouseproject.client.gui.core.misc.MPScrollDirection;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static ru.mousecray.mouseproject.client.gui.core.component.MPGuiRenderHelper.*;
import static ru.mousecray.mouseproject.client.gui.core.event.MPGuiEventFactory.*;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface MPGuiElement<T extends MPGuiElement<T>> {
    T self();
    MPGuiElementCore<T> getCore();

    //Идентификация и иерархия
    void setId(int id);
    int getId();

    default void setScreen(@Nullable MPGuiScreen screen) {
        getCore().setScreen(screen);
        getStateManager().lockForbidden(screen != null || getParent() != null);
    }

    @Nullable default MPGuiScreen getScreen() { return getCore().getScreen(); }

    default void setParent(@Nullable MPGuiPanel<?> parent) {
        getCore().setParent(parent);
        getStateManager().lockForbidden(parent != null || getScreen() != null);
    }

    @Nullable default MPGuiPanel<?> getParent() { return getCore().getParent(); }

    //Данные и состояние
    default String getText() { return getGuiString().get(); }
    default void setText(String text)                         { setGuiString(MPGuiString.simple(text)); }
    default MPGuiString getGuiString()                        { return getCore().getGuiString(); }
    default void setGuiString(MPGuiString guiString)          { getCore().setGuiString(guiString); }

    default boolean isVisible()                               { return !getStateManager().has(MPGuiElementState.HIDDEN); }
    default boolean isEnabled()                               { return !getStateManager().has(MPGuiElementState.DISABLED); }
    default boolean isHovered()                               { return getStateManager().has(MPGuiElementState.HOVERED); }
    default boolean isFocused()                               { return getStateManager().has(MPGuiElementState.FOCUSED); }
    default boolean canBeFocused()                            { return !getStateManager().isForbidden(MPGuiElementState.FOCUSED); }

    default MPGuiElementStateManager getStateManager()        { return getCore().getStateManager(); }

    default MPGuiTexturePack getTexturePack()                 { return getCore().getTexturePack(); }
    default void setTexturePack(MPGuiTexturePack texturePack) { getCore().setTexturePack(texturePack); }
    default MPGuiSoundPack getSoundPack()                     { return getCore().getSoundPack(); }
    default void setSoundPack(MPGuiSoundPack soundPack)       { getCore().setSoundPack(soundPack); }
    default MPGuiColorPack getColorPack()                     { return getCore().getColorPack(); }
    default void setColorPack(MPGuiColorPack colorPack)       { getCore().setColorPack(colorPack); }

    default int getPackedFGColour()                           { return 0; }

    default FontRenderer getFontRenderer() {
        if (getCore().getFontRenderer() != null) return getCore().getFontRenderer();
        if (getScreen() != null) return getScreen().getFontRenderer();
        return Minecraft.getMinecraft().fontRenderer;
    }

    default void setFontRenderer(@Nullable FontRenderer fontRenderer) {
        if (getScreen() != null) {
            MouseProject.LOGGER.warn(
                    "FontRenderer cannot be setup immediately to MPGuiElement that added to container." +
                            " It set now, but actual element size will be updated on the next gui size calculation."
            );
        }
        getCore().setFontRenderer(fontRenderer);
    }

    default MPFontSize getFontSize() {
        if (getCore().getFontSize() != null) return getCore().getFontSize();
        if (getScreen() != null) return getScreen().getFontSize();
        return MPFontSize.NORMAL;
    }

    default void setFontSize(@Nullable MPFontSize fontSize) {
        if (getScreen() != null) {
            MouseProject.LOGGER.warn(
                    "FontSize cannot be setup immediately to MPGuiElement that added to container." +
                            " It set now, but actual element size will be updated on the next gui size calculation."
            );
        }
        getCore().setFontSize(fontSize);
    }

    default float getTextScaleMultiplayer()                 { return getCore().getTextScaleMultiplayer(); }
    default void setTextScaleMultiplayer(float multiplayer) { getCore().setTextScaleMultiplayer(multiplayer); }

    //Геометрия
    default void setShape(IGuiShape shape) { getShape().withShape(shape); }
    default MPMutableGuiShape getShape()                   { return getCore().getShape(); }
    default MPMutableGuiShape getCalculatedShape()         { return getCore().getCalculatedShape(); }
    default MPMutableGuiShape getCalculatedInnerShape()    { return getCore().getCalculatedInnerShape(); }

    default MPGuiScaleRules getScaleRules()                { return getCore().getScaleRules(); }
    default void setScaleRules(MPGuiScaleRules scaleRules) { getCore().setScaleRules(scaleRules); }
    default MPGuiPadding getPadding()                      { return getCore().getPadding(); }
    default void setPadding(MPGuiPadding padding)          { getCore().setPadding(padding); }

    default MPMutableGuiVector getTextOffset()             { return getCore().getTextOffset(); }
    default void setTextOffset(IGuiVector offset)          { getTextOffset().withVector(offset); }

    default void calculate(IGuiVector pDefSize, IGuiVector pContentSize, IGuiShape available) {
        MPMutableGuiShape calcShape = getCalculatedShape();

        calculateFlowComponentShape(
                calcShape, pDefSize, pContentSize, getShape(), getScaleRules(), available
        );

        if (calcShape.width() <= 0 || calcShape.height() <= 0) return;

        MPGuiPadding pad  = getPadding();
        float        padL = calculateFlowComponentX(pDefSize, pContentSize, pad.getLeft());
        float        padT = calculateFlowComponentY(pDefSize, pContentSize, pad.getTop());
        float        padR = calculateFlowComponentX(pDefSize, pContentSize, pad.getRight());
        float        padB = calculateFlowComponentY(pDefSize, pContentSize, pad.getBottom());

        MPMutableGuiShape calcInnerShape = getCalculatedInnerShape();
        calcInnerShape.withShape(calcShape);
        calcInnerShape.grow(-padL, -padT, -padR, -padB);

        calculateTextOffset(pDefSize, pContentSize);

        syncVanilla();

        onCalculated(pDefSize, pContentSize, calcInnerShape);
    }

    default void measurePreferred(IGuiVector pDefSize, IGuiVector pContentSize, float sugX, float sugY, MPMutableGuiVector result) {
        MPGuiScaleRules sr = getScaleRules();
        MPGuiRenderHelper.measurePreferredWithScaleRules(
                pDefSize, pContentSize, sugX, sugY,
                result, getShape(), sr
        );
        MPGuiRenderHelper.addPaddingToPreferred(pDefSize, pContentSize, result, getPadding(), sr);
    }

    default void offsetCalculatedShape(float dx, float dy) {
        getCalculatedShape().offset(dx, dy);
        getCalculatedInnerShape().offset(dx, dy);
        syncVanilla();
        onOffset(dx, dy);
    }

    default void calculateTextOffset(IGuiVector pDefSize, IGuiVector pContentSize) {
        MPGuiRenderHelper.calculateFlowComponentVector(
                getCore().getCalculatedTextOffsetTemp(), pDefSize,
                pContentSize, getTextOffset()
        );
    }

    default void syncVanilla() {
        setupShapeToVanilla(getCalculatedShape());
    }

    default void setupShapeToVanilla(IGuiShape result)                                                { }
    default void onOffset(float dx, float dy)                                                         { }
    default void onCalculated(IGuiVector pDefSize, IGuiVector pContentSize, IGuiShape innerCalcShape) { }

    //Диспетчеризация событий
    default void dispatchUpdate(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        MPGuiElementCore<T> core     = getCore();
        int                 tickDown = core.getTickDown();
        if (tickDown >= 0) core.setTickDown(tickDown + 1);

        MPGuiTickEvent<T> updateEvent = core.getUpdateEvent();
        pushTickEvent(updateEvent, mouseX, mouseY, partialTicks);
        onAnyEventFire(updateEvent);
        if (!updateEvent.isCancelled()) onUpdate(updateEvent);

        MPGuiMouseMoveEvent<T> moveEvent = core.getMoveEvent();
        int                    diffX     = mouseX - moveEvent.getMouseX();
        int                    diffY     = mouseY - moveEvent.getMouseY();
        MPMoveDirection        direction = MPMoveDirection.getMoveDirection(diffX, diffY);
        pushMouseMoveEvent(moveEvent, mouseX, mouseY, direction);

        if (tickDown >= 0 && direction != null) dispatchMouseDragged(mc, mouseX, mouseY, direction, diffX, diffY);
    }

    default void dispatchProcessHover(Minecraft mc, int mouseX, int mouseY) { }

    default void dispatchMouseEnter(Minecraft mc, int mouseX, int mouseY) {
        MPGuiElementCore<T>    core      = getCore();
        MPGuiMouseMoveEvent<T> moveEvent = core.getMoveEvent();
        core.getStateManager().add(MPGuiElementState.HOVERED);
        pushMouseMoveEvent(moveEvent, mouseX, mouseY, MPMoveDirection.calculateMoveDirection(mouseX, mouseY, moveEvent));
        onAnyEventFire(moveEvent);
        if (!moveEvent.isCancelled()) {
            dispatchPlaySound(mc, mc.getSoundHandler(), MPSoundSourceType.ENTER);
            onMouseEnter(moveEvent);
        }
    }

    default void dispatchMouseLeave(Minecraft mc, int mouseX, int mouseY) {
        MPGuiElementCore<T>    core      = getCore();
        MPGuiMouseMoveEvent<T> moveEvent = core.getMoveEvent();
        core.getStateManager().remove(MPGuiElementState.HOVERED);
        pushMouseMoveEvent(moveEvent, mouseX, mouseY, MPMoveDirection.calculateMoveDirection(mouseX, mouseY, moveEvent));
        onAnyEventFire(moveEvent);
        if (!moveEvent.isCancelled()) {
            dispatchPlaySound(mc, mc.getSoundHandler(), MPSoundSourceType.LEAVE);
            onMouseLeave(moveEvent);
        }
    }

    default boolean dispatchMousePressed(Minecraft mc, int mouseX, int mouseY, int mouseButton) {
        MPGuiElementCore<T> core = getCore();
        if (!core.getCalculatedShape().contains(mouseX, mouseY)) return false;
        if (!isEnabled() || !isVisible()) {
            dispatchPlaySound(mc, mc.getSoundHandler(), MPSoundSourceType.DISABLED);
            return false;
        }

        MPGuiElementStateManager stateManager = core.getStateManager();

        if (stateManager.has(MPGuiElementState.FAIL)) {
            dispatchPlaySound(mc, mc.getSoundHandler(), MPSoundSourceType.FAIL);
            return true;
        }

        //Интеграция Forge
        if (getScreen() != null && onForgeClickIntegrationPre()) return true;

        core.setTickDown(0);
        stateManager.add(MPGuiElementState.PRESSED);

        MPGuiMouseClickEvent<T> pressEvent = core.getPressEvent();
        pushMouseClickEvent(pressEvent, mouseX, mouseY);
        onAnyEventFire(pressEvent);
        if (!pressEvent.isCancelled()) {
            dispatchPlaySound(mc, mc.getSoundHandler(), MPSoundSourceType.PRESS);
            onMousePressed(pressEvent);
        }

        //Интеграция Forge
        if (getScreen() != null) onForgeClickIntegrationPost();

        return true;
    }

    default void dispatchMouseReleased(Minecraft mc, int mouseX, int mouseY, int state) {
        MPGuiElementCore<T>      core         = getCore();
        MPGuiElementStateManager stateManager = core.getStateManager();

        core.setTickDown(-1);
        stateManager.remove(MPGuiElementState.PRESSED);

        MPGuiMouseClickEvent<T> releaseEvent = core.getReleaseEvent();

        pushMouseClickEvent(releaseEvent, mouseX, mouseY);
        onAnyEventFire(releaseEvent);

        if (!releaseEvent.isCancelled()) {
            dispatchPlaySound(mc, mc.getSoundHandler(), MPSoundSourceType.RELEASE);
            onMouseReleased(releaseEvent);
            if (core.getCalculatedShape().contains(mouseX, mouseY)) {
                MPGuiMouseClickEvent<T> clickEvent = core.getClickEvent();
                pushMouseClickEvent(clickEvent, mouseX, mouseY);
                onAnyEventFire(clickEvent);
                if (!clickEvent.isCancelled()) {
                    dispatchPlaySound(mc, mc.getSoundHandler(), MPSoundSourceType.CLICK);
                    onClick(clickEvent);
                }
            }
        }
    }

    default boolean dispatchMouseDragged(Minecraft mc, int mouseX, int mouseY, MPMoveDirection dir, int diffX, int diffY) {
        MPGuiElementCore<T>    core      = getCore();
        int                    tickDown  = core.getTickDown();
        MPGuiMouseDragEvent<T> dragEvent = core.getDragEvent();

        if (tickDown >= 0) {
            pushMouseDragEvent(dragEvent, mouseX, mouseY, dir, diffX, diffY, tickDown);
            onAnyEventFire(dragEvent);

            if (!dragEvent.isCancelled()) {
                dispatchPlaySound(mc, mc.getSoundHandler(), MPSoundSourceType.DRAG);
                onMouseDragged(dragEvent);
            }

            return !dragEvent.isCancelled();
        }
        return false;
    }

    default boolean dispatchMouseScrolled(Minecraft mc, int mouseX, int mouseY, int scroll) {
        MPGuiElementCore<T>      core        = getCore();
        MPGuiMouseScrollEvent<T> scrollEvent = core.getScrollEvent();

        MPGuiEventFactory.pushMouseScrollEvent(scrollEvent, mouseX, mouseY, MPScrollDirection.getScrollDirection(scroll), scroll);
        onAnyEventFire(scrollEvent);

        if (!scrollEvent.isCancelled()) {
            dispatchPlaySound(mc, mc.getSoundHandler(), MPSoundSourceType.SCROLL);
            onMouseScrolled(scrollEvent);
        }

        return scrollEvent.isConsumed();
    }

    default boolean dispatchKeyTyped(Minecraft mc, int mouseX, int mouseY, char typedChar, int keyCode) {
        MPGuiElementCore<T> core = getCore();
        if (!core.getStateManager().has(MPGuiElementState.FOCUSED)) return false;

        MPGuiKeyEvent<T> keyEvent = core.getKeyEvent();

        pushKeyEvent(keyEvent, mouseX, mouseY, typedChar, keyCode);
        onAnyEventFire(keyEvent);

        if (!keyEvent.isCancelled()) {
            dispatchPlaySound(mc, mc.getSoundHandler(), MPSoundSourceType.KEY_TYPED);
            onKeyTyped(keyEvent);
        }

        return keyEvent.isConsumed();
    }

    default void dispatchPlaySound(Minecraft mc, SoundHandler soundHandler, MPSoundSourceType source) {

        SoundEvent sound = getSoundPack().getSound(source);
        if (sound != null) {
            MPGuiSoundEvent<T>     soundEvent = getCore().getSoundEvent();
            MPGuiMouseMoveEvent<T> moveEvent  = getCore().getMoveEvent();
            pushSoundEvent(soundEvent, moveEvent.getMouseX(), moveEvent.getMouseY(), soundHandler, sound, source);
            onAnyEventFire(soundEvent);
            if (!soundEvent.isCancelled()) onPlaySound(soundEvent);
        }
    }

    //Рендеринг
    default void dispatchDraw(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (isVisible()) {
            dispatchDrawBackground(mc, mouseX, mouseY, partialTicks);
            dispatchDrawForeground(mc, mouseX, mouseY, partialTicks);
            dispatchDrawText(mc, mouseX, mouseY, partialTicks);
            dispatchDrawLast(mc, mouseX, mouseY, partialTicks);
        }
    }

    default void dispatchDrawBackground(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        MPGuiTickEvent<T> drawBGEvent = getCore().getDrawBGEvent();
        MPGuiEventFactory.pushTickEvent(drawBGEvent, mouseX, mouseY, partialTicks);
        onAnyEventFire(drawBGEvent);
        if (!drawBGEvent.isCancelled()) onDrawBackground(drawBGEvent);
    }

    default void dispatchDrawForeground(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        MPGuiTickEvent<T> drawFGEvent = getCore().getDrawFGEvent();
        MPGuiEventFactory.pushTickEvent(drawFGEvent, mouseX, mouseY, partialTicks);
        onAnyEventFire(drawFGEvent);
        if (!drawFGEvent.isCancelled()) onDrawForeground(drawFGEvent);
    }

    default void dispatchDrawText(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        MPGuiTickEvent<T> drawTextEvent = getCore().getDrawTextEvent();
        MPGuiEventFactory.pushTickEvent(drawTextEvent, mouseX, mouseY, partialTicks);
        onAnyEventFire(drawTextEvent);
        if (!drawTextEvent.isCancelled()) onDrawText(drawTextEvent);
    }

    default void dispatchDrawLast(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        MPGuiTickEvent<T> drawLastEvent = getCore().getDrawLastEvent();
        MPGuiEventFactory.pushTickEvent(drawLastEvent, mouseX, mouseY, partialTicks);
        onAnyEventFire(drawLastEvent);
        if (!drawLastEvent.isCancelled()) onDrawLast(drawLastEvent);
    }

    //Интеграция с vanilla
    default int getHoverState(boolean mouseOver) { return !isEnabled() ? 0 : mouseOver ? 2 : 1; }
    default boolean mouseHover(Minecraft mc, int mouseX, int mouseY)   { return getCalculatedShape().contains(mouseX, mouseY); }
    default boolean mousePressed(Minecraft mc, int mouseX, int mouseY) { return isEnabled() && isVisible() && getCalculatedShape().contains(mouseX, mouseY); }
    default void mouseReleased(int mouseX, int mouseY)                 { dispatchMouseReleased(Minecraft.getMinecraft(), mouseX, mouseY, 0); }
    default void playPressSound(SoundHandler soundHandler)             { dispatchPlaySound(Minecraft.getMinecraft(), Minecraft.getMinecraft().getSoundHandler(), MPSoundSourceType.PRESS); }
    default boolean isMouseOver()                                      { return getStateManager().has(MPGuiElementState.HOVERED); }

    default void performClickFromVanilla() {
        if (!isEnabled() || !isVisible()) return;

        MPMutableGuiShape calcShape = getCalculatedShape();
        int               centerX   = (int) (calcShape.x() + calcShape.width() / 2f);
        int               centerY   = (int) (calcShape.y() + calcShape.height() / 2f);

        Minecraft mc = Minecraft.getMinecraft();
        dispatchMousePressed(mc, centerX, centerY, 0);
        dispatchMouseReleased(mc, centerX, centerY, 0);
    }

    //true if event is default logic
    default boolean onForgeClickIntegrationPre() { return false; }
    default void onForgeClickIntegrationPost() { }

    //Обработчики событий
    default void onDrawBackground(MPGuiTickEvent<T> event) {
        MPGuiElementCore<T> core            = getCore();
        List<MPGuiTexture>  textures        = core.getTexturePack().getCalculatedTextures(core.getStateManager());
        MPMutableGuiShape   calculatedShape = core.getCalculatedShape();
        for (MPGuiTexture texture : textures) {
            texture.draw(
                    event.getMc(),
                    calculatedShape.x(), calculatedShape.y(),
                    calculatedShape.width(), calculatedShape.height()
            );
        }
    }

    default void onDrawForeground(MPGuiTickEvent<T> event) { }

    default void onDrawText(MPGuiTickEvent<T> event) {
        MPGuiElementCore<T> core = getCore();

        String text = core.getGuiString().get();
        if (text != null && !text.isEmpty()) {
            int          color = core.getColorPack().getCalculatedColor(core.getStateManager(), getPackedFGColour());
            FontRenderer fr    = getFontRenderer();
            MPFontSize   fs    = getFontSize();

            float scale    = fs.getScale() * getTextScaleMultiplayer();
            float invScale = 1.0F / scale;

            MPMutableGuiShape calculatedInnerShape = core.getCalculatedInnerShape();

            float innerX = calculatedInnerShape.x();
            float innerY = calculatedInnerShape.y();
            float innerW = calculatedInnerShape.width();
            float innerH = calculatedInnerShape.height();

            GlStateManager.pushMatrix();
            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

            GlStateManager.scale(scale, scale, 1.0F);

            MPMutableGuiVector calculatedTextOffsetTemp = core.getCalculatedTextOffsetTemp();
            MPGuiRenderHelper.drawCenteredString(
                    fr, text,
                    (innerX + innerW / 2f) * invScale + calculatedTextOffsetTemp.x() * invScale,
                    (innerY + innerH / 2f) * invScale - (fr.FONT_HEIGHT / 2f) + calculatedTextOffsetTemp.y() * invScale,
                    color,
                    fs != MPFontSize.SMALL
            );

            GlStateManager.popMatrix();
        }
    }

    default void onDrawLast(MPGuiTickEvent<T> event)             { }

    default void onUpdate(MPGuiTickEvent<T> event)               { }
    default void onAnyEventFire(MPGuiEvent<T> event)             { }
    default void onMouseEnter(MPGuiMouseMoveEvent<T> event)      { }
    default void onMouseLeave(MPGuiMouseMoveEvent<T> event)      { }
    default void onMousePressed(MPGuiMouseClickEvent<T> event)   { }
    default void onMouseReleased(MPGuiMouseClickEvent<T> event)  { }
    default void onMouseDragged(MPGuiMouseDragEvent<T> event)    { }
    default void onMouseScrolled(MPGuiMouseScrollEvent<T> event) { }
    default void onKeyTyped(MPGuiKeyEvent<T> event)              { }
    default void onClick(MPGuiMouseClickEvent<T> event)          { }

    default void onPlaySound(MPGuiSoundEvent<T> event) {
        event.getHandler().playSound(PositionedSoundRecord.getMasterRecord(event.getSound(), 1.0F));
    }
}