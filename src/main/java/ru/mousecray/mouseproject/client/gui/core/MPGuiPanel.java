/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mouseproject.client.gui.core;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiTextField;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.mouseproject.MouseProject;
import ru.mousecray.mouseproject.client.gui.core.component.MPGuiRenderHelper;
import ru.mousecray.mouseproject.client.gui.core.component.color.MPGuiColorPack;
import ru.mousecray.mouseproject.client.gui.core.component.lang.MPGuiString;
import ru.mousecray.mouseproject.client.gui.core.component.sound.MPSoundSourceType;
import ru.mousecray.mouseproject.client.gui.core.component.state.MPGuiElementState;
import ru.mousecray.mouseproject.client.gui.core.dim.*;
import ru.mousecray.mouseproject.client.gui.core.dim.layout.MPGuiLayoutParams;
import ru.mousecray.mouseproject.client.gui.core.dim.layout.MPGuiMargin;
import ru.mousecray.mouseproject.client.gui.core.event.*;
import ru.mousecray.mouseproject.client.gui.core.misc.MPFontSize;
import ru.mousecray.mouseproject.client.gui.core.misc.MPMoveDirection;
import ru.mousecray.mouseproject.client.gui.core.misc.MPScrollDirection;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;


@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class MPGuiPanel<T extends MPGuiPanel<T>> implements MPGuiElement<T> {
    protected final MPGuiElementCore<T> core;

    protected final List<MPGuiElement<?>> children = new ArrayList<>();

    protected final MPMutableGuiVector measureTemp        = new MPMutableGuiVector();
    protected final MPMutableGuiShape  childAvailableTemp = new MPMutableGuiShape();
    protected final MPMutableGuiShape  innerShapeTemp     = new MPMutableGuiShape();
    protected final float[]            marginTemp         = new float[4];

    private int id;

    @Nullable private MPGuiElement<?> lastHoveredElement  = null;
    @Nullable private MPGuiElement<?> lastSelectedElement = null;

    public MPGuiPanel(MPGuiShape shape) {
        core = new MPGuiElementCore<>(shape);
        core.bindEvents(Minecraft.getMinecraft(), self());

        getStateManager().setForbidden(MPGuiElementState.FOCUSED, true);
        setColorPack(MPGuiColorPack.CONTROL_SIMPLE());
    }

    public List<MPGuiElement<?>> getChildren()               { return children; }

    @SuppressWarnings("unchecked") @Override public T self() { return (T) this; }
    @Override public MPGuiElementCore<T> getCore()           { return core; }
    @Override public void setId(int id)                      { this.id = id; }
    @Override public int getId()                             { return id; }

    @Override
    public void setScreen(@Nullable MPGuiScreen screen) {
        MPGuiElement.super.setScreen(screen);
        for (MPGuiElement<?> child : children) child.setScreen(screen);
    }

    @Override public MPGuiString getGuiString()               { return MPGuiString.EMPTY(); }
    @Override public void setGuiString(MPGuiString guiString) { }

    @Override
    public FontRenderer getFontRenderer() {
        if (getScreen() != null) return getScreen().getFontRenderer();
        return Minecraft.getMinecraft().fontRenderer;
    }

    @Override
    public void setFontRenderer(@Nullable FontRenderer fr) {
        MouseProject.LOGGER.warn("MPGuiPanel cannot support custom FontRenderer");
    }

    @Override public MPFontSize getFontSize() { return MPFontSize.NORMAL; }

    @Override
    public void setFontSize(@Nullable MPFontSize size) {
        MouseProject.LOGGER.warn("MPGuiPanel cannot support custom FontSize");
    }

    @Override public float getTextScaleMultiplayer() { return 0.0f; }

    @Override
    public void setTextScaleMultiplayer(float multiplayer) {
        MouseProject.LOGGER.warn("MPGuiPanel cannot support custom TextScaleMultiplayer");
    }

    @Override public MPMutableGuiVector getTextOffset() { return new MPMutableGuiVector(); }

    public void addChild(MPGuiElement<?> child, @Nullable MPGuiMargin margin, @Nullable MPAnchorPos anchor, @Nullable MPGuiVector offset) {
        children.add(child);

        child.getCore().setLayoutParams(new MPGuiLayoutParams(margin, anchor, offset));

        child.setParent(this);
        if (getScreen() != null) {
            child.setScreen(getScreen());
            child.setId(getScreen().genNextElementID());
        }
    }

    public void addChild(MPGuiElement<?> child)                 { addChild(child, MPGuiMargin.ZERO(), MPAnchorPos.TOP_LEFT, MPGuiVector.ZERO); }

    protected MPGuiMargin getChildMargin(MPGuiElement<?> child) { return child.getCore().getLayoutParams().margin; }
    protected MPGuiVector getChildOffset(MPGuiElement<?> child) { return child.getCore().getLayoutParams().offset; }

    @Nullable
    public MPGuiElement<?> getLastSelectedElementRecursively() {
        if (lastSelectedElement instanceof MPGuiPanel) {
            MPGuiElement<?> nested = ((MPGuiPanel<?>) lastSelectedElement).getLastSelectedElementRecursively();
            return nested != null ? nested : lastSelectedElement;
        }
        return lastSelectedElement;
    }

    protected float[] calculateScaledOffset(MPGuiElement<?> child, IGuiVector parentDefaultSize, IGuiVector parentContentSize) {
        MPGuiVector offset = getChildOffset(child);
        return new float[]{
                MPGuiRenderHelper.calculateFlowComponentX(parentDefaultSize, parentContentSize, offset.x()),
                MPGuiRenderHelper.calculateFlowComponentY(parentDefaultSize, parentContentSize, offset.y())
        };
    }

    @Override
    public void onCalculated(IGuiVector pDefSize, IGuiVector pContentSize, IGuiShape innerCalcShape) {
        innerShapeTemp.withShape(innerCalcShape);
        layoutChildren(pDefSize, pContentSize, innerShapeTemp);
    }

    protected abstract void layoutChildren(IGuiVector parentDefaultSize, IGuiVector parentContentSize, MPMutableGuiShape inner);

    @Override
    public void offsetCalculatedShape(float dx, float dy) {
        MPGuiElement.super.offsetCalculatedShape(dx, dy);
        for (MPGuiElement<?> child : children) child.offsetCalculatedShape(dx, dy);
    }

    //Диспетчеризация событий
    @Override
    public final void dispatchUpdate(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        MPGuiElement.super.dispatchUpdate(mc, mouseX, mouseY, partialTicks);
        for (MPGuiElement<?> child : children) child.dispatchUpdate(mc, mouseX, mouseY, partialTicks);
    }

    @Override
    public final void dispatchProcessHover(Minecraft mc, int mouseX, int mouseY) {
        MPGuiElement.super.dispatchProcessHover(mc, mouseX, mouseY);

        if (!isVisible()) return;

        MPGuiElement<?> currentHovered = null;

        for (int k = children.size() - 1; k >= 0; --k) {
            MPGuiElement<?> child = children.get(k);
            if (child.mouseHover(mc, mouseX, mouseY) && child.isVisible()) {
                currentHovered = child;
                break;
            }
        }

        if (lastHoveredElement != currentHovered) {
            if (lastHoveredElement != null) lastHoveredElement.dispatchMouseLeave(mc, mouseX, mouseY);
            if (currentHovered != null) currentHovered.dispatchMouseEnter(mc, mouseX, mouseY);
            lastHoveredElement = currentHovered;
        }

        if (currentHovered != null) currentHovered.dispatchProcessHover(mc, mouseX, mouseY);
    }

    @Override
    public final void dispatchMouseLeave(Minecraft mc, int mouseX, int mouseY) {
        if (lastHoveredElement != null) {
            lastHoveredElement.dispatchMouseLeave(mc, mouseX, mouseY);
            lastHoveredElement = null;
        }

        MPGuiElement.super.dispatchMouseLeave(mc, mouseX, mouseY);
    }

    @Override
    public final boolean dispatchMousePressed(Minecraft mc, int mouseX, int mouseY, int mouseButton) {
        if (!isEnabled() || !isVisible()) {
            if (getCalculatedShape().contains(mouseX, mouseY)) {
                dispatchPlaySound(mc, mc.getSoundHandler(), MPSoundSourceType.DISABLED);
            }
            return false;
        }

        MPGuiMouseClickEvent<T> pressEvent = getCore().getPressEvent();
        MPGuiEventFactory.pushMouseClickEvent(pressEvent, mouseX, mouseY);
        onAnyEventFire(pressEvent);
        if (pressEvent.isCancelled()) return true;

        for (int k = children.size() - 1; k >= 0; k--) {
            MPGuiElement<?> child = children.get(k);
            if (child.dispatchMousePressed(mc, mouseX, mouseY, mouseButton)) {
                lastSelectedElement = child;
                return true;
            }
        }

        if (!getCalculatedShape().contains(mouseX, mouseY)) return false;

        if (getStateManager().has(MPGuiElementState.FAIL)) dispatchPlaySound(mc, mc.getSoundHandler(), MPSoundSourceType.FAIL);

        getCore().setTickDown(0);
        getStateManager().add(MPGuiElementState.PRESSED);

        dispatchPlaySound(mc, mc.getSoundHandler(), MPSoundSourceType.PRESS);
        onMousePressed(pressEvent);
        return true;
    }

    @Override
    public final void dispatchMouseReleased(Minecraft mc, int mouseX, int mouseY, int state) {
        if (lastSelectedElement != null) {
            lastSelectedElement.dispatchMouseReleased(mc, mouseX, mouseY, state);
            lastSelectedElement = null;
            return;
        }

        getCore().setTickDown(-1);
        getStateManager().remove(MPGuiElementState.PRESSED);

        MPGuiMouseClickEvent<T> releaseEvent = getCore().getReleaseEvent();
        MPGuiEventFactory.pushMouseClickEvent(releaseEvent, mouseX, mouseY);
        onAnyEventFire(releaseEvent);
        if (!releaseEvent.isCancelled()) {
            dispatchPlaySound(mc, mc.getSoundHandler(), MPSoundSourceType.RELEASE);
            onMouseReleased(releaseEvent);
            if (getCalculatedShape().contains(mouseX, mouseY)) {
                MPGuiMouseClickEvent<T> clickEvent = getCore().getClickEvent();
                MPGuiEventFactory.pushMouseClickEvent(clickEvent, mouseX, mouseY);
                onAnyEventFire(clickEvent);
                if (!clickEvent.isCancelled()) {
                    dispatchPlaySound(mc, mc.getSoundHandler(), MPSoundSourceType.CLICK);
                    onClick(clickEvent);
                }
            }
        }
    }

    @Override
    public final boolean dispatchMouseDragged(Minecraft mc, int mouseX, int mouseY, MPMoveDirection dir, int diffX, int diffY) {
        if (lastSelectedElement != null) {
            return lastSelectedElement.dispatchMouseDragged(mc, mouseX, mouseY, dir, diffX, diffY);
        }

        int tickDown = getCore().getTickDown();
        if (tickDown >= 0) {
            MPGuiMouseDragEvent<T> dragEvent = getCore().getDragEvent();
            MPGuiEventFactory.pushMouseDragEvent(dragEvent, mouseX, mouseY, dir, diffX, diffY, tickDown);
            onAnyEventFire(dragEvent);
            if (!dragEvent.isCancelled()) {
                dispatchPlaySound(mc, mc.getSoundHandler(), MPSoundSourceType.DRAG);
                onMouseDragged(dragEvent);
            }
            return !dragEvent.isCancelled();
        }
        return false;
    }

    @Override
    public final boolean dispatchMouseScrolled(Minecraft mc, int mouseX, int mouseY, int scroll) {
        if (!getCalculatedShape().contains(mouseX, mouseY) || !isVisible()) return false;

        for (int k = children.size() - 1; k >= 0; k--) {
            MPGuiElement<?> child = children.get(k);
            if (child.isVisible() && child.getCalculatedShape().contains(mouseX, mouseY)) {
                if (child.dispatchMouseScrolled(mc, mouseX, mouseY, scroll)) return true;
            }
        }

        MPGuiMouseScrollEvent<T> scrollEvent = getCore().getScrollEvent();
        MPGuiEventFactory.pushMouseScrollEvent(
                scrollEvent, mouseX, mouseY, MPScrollDirection.getScrollDirection(scroll), scroll
        );
        onAnyEventFire(scrollEvent);
        if (!scrollEvent.isCancelled()) {
            dispatchPlaySound(mc, mc.getSoundHandler(), MPSoundSourceType.SCROLL);
            onMouseScrolled(scrollEvent);
        }

        return scrollEvent.isConsumed();
    }

    @Override
    public final boolean dispatchKeyTyped(Minecraft mc, int mouseX, int mouseY, char typedChar, int keyCode) {
        if (!isVisible()) return false;

        for (int k = children.size() - 1; k >= 0; k--) {
            MPGuiElement<?> child = children.get(k);
            if (child.dispatchKeyTyped(mc, mouseX, mouseY, typedChar, keyCode)) return true;
        }

        if (getStateManager().has(MPGuiElementState.FOCUSED)) {
            MPGuiKeyEvent<T> keyEvent = getCore().getKeyEvent();
            MPGuiEventFactory.pushKeyEvent(keyEvent, mouseX, mouseY, typedChar, keyCode);
            onAnyEventFire(keyEvent);

            if (!keyEvent.isCancelled()) {
                dispatchPlaySound(mc, mc.getSoundHandler(), MPSoundSourceType.KEY_TYPED);
                onKeyTyped(keyEvent);
            }

            return keyEvent.isConsumed();
        }
        return false;
    }

    //Рендеринг
    @Override
    public final void dispatchDrawBackground(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        MPGuiTickEvent<T> drawBGEvent = getCore().getDrawBGEvent();
        MPGuiEventFactory.pushTickEvent(drawBGEvent, mouseX, mouseY, partialTicks);
        onAnyEventFire(drawBGEvent);
        if (!drawBGEvent.isCancelled()) {
            onDrawBackground(drawBGEvent);
            for (MPGuiElement<?> child : children) {
                child.dispatchDrawBackground(mc, mouseX, mouseY, partialTicks);
            }
        }
    }

    @Override
    public final void dispatchDrawForeground(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        MPGuiTickEvent<T> drawFGEvent = getCore().getDrawFGEvent();
        MPGuiEventFactory.pushTickEvent(drawFGEvent, mouseX, mouseY, partialTicks);
        onAnyEventFire(drawFGEvent);
        if (!drawFGEvent.isCancelled()) {
            onDrawForeground(drawFGEvent);
            for (MPGuiElement<?> child : children) {
                child.dispatchDrawForeground(mc, mouseX, mouseY, partialTicks);
            }
        }
    }

    @Override
    public final void dispatchDrawText(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        MPGuiTickEvent<T> drawTextEvent = getCore().getDrawTextEvent();
        MPGuiEventFactory.pushTickEvent(drawTextEvent, mouseX, mouseY, partialTicks);
        onAnyEventFire(drawTextEvent);
        if (!drawTextEvent.isCancelled()) {
            onDrawText(drawTextEvent);
            for (MPGuiElement<?> child : children) {
                child.dispatchDrawText(mc, mouseX, mouseY, partialTicks);
            }
        }
    }

    @Override
    public final void dispatchDrawLast(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        MPGuiTickEvent<T> drawLastEvent = getCore().getDrawLastEvent();
        MPGuiEventFactory.pushTickEvent(drawLastEvent, mouseX, mouseY, partialTicks);
        onAnyEventFire(drawLastEvent);
        if (!drawLastEvent.isCancelled()) {
            onDrawLast(drawLastEvent);
            for (MPGuiElement<?> child : children) {
                child.dispatchDrawLast(mc, mouseX, mouseY, partialTicks);
            }
        }
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

    public void collectElements() {
        MPGuiScreen screen = getScreen();
        if (screen == null) return;
        for (MPGuiElement<?> child : children) {
            if (child instanceof MPGuiPanel) ((MPGuiPanel<?>) child).collectElements();
            else if (child instanceof GuiButton) screen.getButtonList().add((GuiButton) child);
            else if (child instanceof GuiLabel) screen.getLabelList().add((GuiLabel) child);
            else if (child instanceof GuiTextField) screen.getFieldsList().add((GuiTextField) child);
        }
    }

    public void removeAllChildren() {
        children.clear();
        onChildrenCleared();
    }

    protected void onChildrenCleared() { }
}