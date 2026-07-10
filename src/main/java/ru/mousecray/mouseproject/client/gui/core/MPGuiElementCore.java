/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mouseproject.client.gui.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import ru.mousecray.mouseproject.client.gui.core.component.color.MPGuiColorPack;
import ru.mousecray.mouseproject.client.gui.core.component.lang.MPGuiString;
import ru.mousecray.mouseproject.client.gui.core.component.sound.MPGuiSoundPack;
import ru.mousecray.mouseproject.client.gui.core.component.state.MPGuiElementStateManager;
import ru.mousecray.mouseproject.client.gui.core.component.texture.MPGuiTexturePack;
import ru.mousecray.mouseproject.client.gui.core.dim.*;
import ru.mousecray.mouseproject.client.gui.core.dim.layout.MPGuiLayoutParams;
import ru.mousecray.mouseproject.client.gui.core.dim.layout.MPGuiPadding;
import ru.mousecray.mouseproject.client.gui.core.event.*;
import ru.mousecray.mouseproject.client.gui.core.misc.MPClickType;
import ru.mousecray.mouseproject.client.gui.core.misc.MPFontSize;

import javax.annotation.Nullable;
import java.util.Objects;

public class MPGuiElementCore<T extends MPGuiElement<T>> {
    private final MPGuiTickEvent<T>
            updateEvent   = new MPGuiTickEvent<>(),
            drawBGEvent   = new MPGuiTickEvent<>(),
            drawFGEvent   = new MPGuiTickEvent<>(),
            drawLastEvent = new MPGuiTickEvent<>(),
            drawTextEvent = new MPGuiTickEvent<>();
    private final MPGuiMouseClickEvent<T>
            pressEvent   = new MPGuiMouseClickEvent<>(MPClickType.PRESS),
            releaseEvent = new MPGuiMouseClickEvent<>(MPClickType.RELEASE),
            clickEvent   = new MPGuiMouseClickEvent<>(MPClickType.CLICK);
    private final MPGuiMouseMoveEvent<T>   moveEvent   = new MPGuiMouseMoveEvent<>();
    private final MPGuiMouseDragEvent<T>   dragEvent   = new MPGuiMouseDragEvent<>();
    private final MPGuiMouseScrollEvent<T> scrollEvent = new MPGuiMouseScrollEvent<>();
    private final MPGuiKeyEvent<T>         keyEvent    = new MPGuiKeyEvent<>();
    private final MPGuiSoundEvent<T>       soundEvent  = new MPGuiSoundEvent<>();

    private final MPGuiElementStateManager stateManager             = new MPGuiElementStateManager();
    private final MPMutableGuiShape        shape;
    private final MPMutableGuiShape        calculatedShape          = new MPMutableGuiShape();
    private final MPMutableGuiShape        calculatedInnerShape     = new MPMutableGuiShape();
    private final MPMutableGuiVector       calculatedTextOffsetTemp = new MPMutableGuiVector();
    private final MPMutableGuiVector       textOffset               = new MPMutableGuiVector();

    private MPGuiTexturePack texturePack = MPGuiTexturePack.EMPTY();
    private MPGuiColorPack   colorPack   = MPGuiColorPack.EMPTY();
    private MPGuiSoundPack   soundPack   = MPGuiSoundPack.EMPTY();

    private MPGuiString     guiString  = MPGuiString.EMPTY();
    private MPGuiScaleRules scaleRules = new MPGuiScaleRules(MPGuiScaleType.FLOW);
    private MPGuiPadding    padding    = new MPGuiPadding(0);

    private int   tickDown             = -1;
    private float textScaleMultiplayer = 1.0F;

    @Nullable private MPGuiScreen   screen;
    @Nullable private MPGuiPanel<?> parent;
    @Nullable private FontRenderer  fontRenderer;
    @Nullable private MPFontSize    fontSize;

    private MPGuiLayoutParams layoutParams = new MPGuiLayoutParams(null, null, null);

    public MPGuiElementCore(MPGuiShape shape) {
        this.shape = shape.toMutable();
    }

    public void bindEvents(Minecraft mc, T th) {
        updateEvent.bind(mc, th);
        drawBGEvent.bind(mc, th);
        drawFGEvent.bind(mc, th);
        drawLastEvent.bind(mc, th);
        drawTextEvent.bind(mc, th);
        pressEvent.bind(mc, th);
        releaseEvent.bind(mc, th);
        clickEvent.bind(mc, th);
        moveEvent.bind(mc, th);
        dragEvent.bind(mc, th);
        scrollEvent.bind(mc, th);
        keyEvent.bind(mc, th);
        soundEvent.bind(mc, th);
    }

    public MPGuiTickEvent<T> getUpdateEvent()               { return updateEvent; }
    public MPGuiTickEvent<T> getDrawBGEvent()               { return drawBGEvent; }
    public MPGuiTickEvent<T> getDrawFGEvent()               { return drawFGEvent; }
    public MPGuiTickEvent<T> getDrawLastEvent()             { return drawLastEvent; }
    public MPGuiTickEvent<T> getDrawTextEvent()             { return drawTextEvent; }
    public MPGuiMouseClickEvent<T> getPressEvent()          { return pressEvent; }
    public MPGuiMouseClickEvent<T> getReleaseEvent()        { return releaseEvent; }
    public MPGuiMouseClickEvent<T> getClickEvent()          { return clickEvent; }
    public MPGuiMouseMoveEvent<T> getMoveEvent()            { return moveEvent; }
    public MPGuiMouseDragEvent<T> getDragEvent()            { return dragEvent; }
    public MPGuiMouseScrollEvent<T> getScrollEvent()        { return scrollEvent; }
    public MPGuiKeyEvent<T> getKeyEvent()                   { return keyEvent; }
    public MPGuiSoundEvent<T> getSoundEvent()               { return soundEvent; }

    public MPGuiElementStateManager getStateManager()       { return stateManager; }
    public MPMutableGuiShape getShape()                     { return shape; }
    public MPMutableGuiShape getCalculatedShape()           { return calculatedShape; }
    public MPMutableGuiShape getCalculatedInnerShape()      { return calculatedInnerShape; }
    public MPMutableGuiVector getCalculatedTextOffsetTemp() { return calculatedTextOffsetTemp; }
    public MPMutableGuiVector getTextOffset()               { return textOffset; }

    public MPGuiString getGuiString()                       { return guiString; }

    public void setGuiString(MPGuiString guiString) {
        Objects.requireNonNull(guiString, "guiString cannot be null. Use MPGuiString.EMPTY() instead.");
        this.guiString = guiString;
    }

    public MPGuiTexturePack getTexturePack() { return texturePack; }

    public void setTexturePack(MPGuiTexturePack texturePack) {
        Objects.requireNonNull(texturePack, "texturePack cannot be null. Use MPGuiTexturePack.EMPTY() instead.");
        this.texturePack = texturePack;
    }

    public MPGuiColorPack getColorPack() { return colorPack; }

    public void setColorPack(MPGuiColorPack colorPack) {
        Objects.requireNonNull(colorPack, "colorPack cannot be null. Use MPGuiColorPack.EMPTY() instead.");
        this.colorPack = colorPack;
    }

    public MPGuiSoundPack getSoundPack() { return soundPack; }

    public void setSoundPack(MPGuiSoundPack soundPack) {
        Objects.requireNonNull(soundPack, "soundPack cannot be null. Use MPGuiSoundPack.EMPTY() instead.");
        this.soundPack = soundPack;
    }

    public MPGuiScaleRules getScaleRules() { return scaleRules; }

    public void setScaleRules(MPGuiScaleRules scaleRules) {
        Objects.requireNonNull(scaleRules, "scaleRules cannot be null.");
        this.scaleRules = scaleRules;
    }

    public MPGuiPadding getPadding() { return padding; }

    public void setPadding(MPGuiPadding padding) {
        Objects.requireNonNull(padding, "padding cannot be null. Use MPGuiPadding.DEFAULT() instead.");
        this.padding = padding;
    }

    public MPGuiLayoutParams getLayoutParams() { return layoutParams; }
    public void setLayoutParams(MPGuiLayoutParams layoutParams) {
        this.layoutParams = layoutParams != null ? layoutParams : new MPGuiLayoutParams(null, null, null);
    }

    public int getTickDown()                                         { return tickDown; }
    public void setTickDown(int tickDown)                            { this.tickDown = tickDown; }

    public float getTextScaleMultiplayer()                           { return textScaleMultiplayer; }
    public void setTextScaleMultiplayer(float textScaleMultiplayer)  { this.textScaleMultiplayer = textScaleMultiplayer; }

    @Nullable public MPGuiScreen getScreen()                         { return screen; }
    public void setScreen(@Nullable MPGuiScreen screen)              { this.screen = screen; }
    @Nullable public MPGuiPanel<?> getParent()                       { return parent; }
    public void setParent(@Nullable MPGuiPanel<?> parent)            { this.parent = parent; }

    @Nullable public FontRenderer getFontRenderer()                  { return fontRenderer; }
    public void setFontRenderer(@Nullable FontRenderer fontRenderer) { this.fontRenderer = fontRenderer; }
    @Nullable public MPFontSize getFontSize()                        { return fontSize; }
    public void setFontSize(@Nullable MPFontSize fontSize)           { this.fontSize = fontSize; }
}