/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mouseproject.client.gui.core;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.mouseproject.MouseProject;
import ru.mousecray.mouseproject.client.gui.core.component.MPGuiRenderHelper;
import ru.mousecray.mouseproject.client.gui.core.component.lang.MPGuiString;
import ru.mousecray.mouseproject.client.gui.core.component.sound.MPSoundSourceType;
import ru.mousecray.mouseproject.client.gui.core.component.state.MPGuiElementState;
import ru.mousecray.mouseproject.client.gui.core.control.MPGuiScrollbar;
import ru.mousecray.mouseproject.client.gui.core.dim.*;
import ru.mousecray.mouseproject.client.gui.core.dim.layout.MPGuiPadding;
import ru.mousecray.mouseproject.client.gui.core.event.*;
import ru.mousecray.mouseproject.client.gui.core.misc.MPFontSize;
import ru.mousecray.mouseproject.client.gui.core.misc.MPMoveDirection;
import ru.mousecray.mouseproject.client.gui.core.misc.MPScrollDirection;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static ru.mousecray.mouseproject.client.gui.core.component.MPGuiRenderHelper.calculateFlowComponentX;
import static ru.mousecray.mouseproject.client.gui.core.component.MPGuiRenderHelper.calculateFlowComponentY;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class MPGuiScrollPanel<T extends MPGuiScrollPanel<T>> implements MPGuiElement<T> {
    protected final MPGuiElementCore<T> core;

    @Nullable private MPGuiPanel<?>  content;
    private           MPGuiScrollbar scrollbar;
    private           MPOrientation  orientation = MPOrientation.VERTICAL;

    private final MPGuiRenderHelper.ScissorState scissorState = new MPGuiRenderHelper.ScissorState();

    private float   scrollValue        = 0;
    private float   contentSize        = 0;
    private boolean scrollEnabled      = true;
    private float   scrollbarThickness = 8f;

    private int id;

    public MPGuiScrollPanel(MPGuiShape shape) {
        core = new MPGuiElementCore<>(shape);
        core.bindEvents(Minecraft.getMinecraft(), self());

        MPGuiShape sbShape = new MPGuiShape(0, 0, scrollbarThickness, 100);
        scrollbar = new MPGuiScrollbar(sbShape);

        scrollbar.setOnScroll(val -> {
            float diff = val - scrollValue;
            scrollValue = val;
            if (content != null) {
                if (orientation == MPOrientation.VERTICAL) content.offsetCalculatedShape(0, -diff);
                else content.offsetCalculatedShape(-diff, 0);
            }
        });
    }

    public MPOrientation getOrientation() { return orientation; }

    public void setOrientation(MPOrientation orientation) {
        this.orientation = orientation;
        if (scrollbar != null) {
            scrollbar.setOrientation(orientation);
            if (orientation == MPOrientation.VERTICAL) scrollbar.getShape().withWidth(scrollbarThickness);
            else scrollbar.getShape().withHeight(scrollbarThickness);
        }
    }

    public void setScrollbarThickness(float thickness) {
        scrollbarThickness = thickness;
        if (scrollbar != null) {
            if (orientation == MPOrientation.VERTICAL) scrollbar.getShape().withWidth(thickness);
            else scrollbar.getShape().withHeight(thickness);
        }
    }

    public MPGuiScrollbar getScrollbar()                { return scrollbar; }
    public void setScrollEnabled(boolean scrollEnabled) { this.scrollEnabled = scrollEnabled; }

    public void setContent(@Nullable MPGuiPanel<?> content) {
        this.content = content;
        if (content != null) {
            MPGuiPanel<?> parent = getParent();
            MPGuiScreen   screen = getScreen();
            if (parent != null) content.setParent(parent);
            if (screen != null) {
                content.setScreen(screen);
                content.setId(screen.genNextElementID());
            }
        }
    }

    @Nullable public MPGuiPanel<?> getContent()              { return content; }

    @SuppressWarnings("unchecked") @Override public T self() { return (T) this; }
    @Override public MPGuiElementCore<T> getCore()           { return core; }
    @Override public void setId(int id)                      { this.id = id; }
    @Override public int getId()                             { return id; }

    @Override
    public void setScreen(@Nullable MPGuiScreen screen) {
        MPGuiElement.super.setScreen(screen);
        if (content != null) content.setScreen(screen);
        if (scrollbar != null) scrollbar.setScreen(screen);
    }

    @Override
    public void setParent(@Nullable MPGuiPanel<?> parent) {
        MPGuiElement.super.setParent(parent);
        if (parent != null && content != null) content.setParent(parent);
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
        MouseProject.LOGGER.warn("MPGuiScrollPanel cannot support custom FontRenderer");
    }

    @Override public MPFontSize getFontSize() { return MPFontSize.NORMAL; }

    @Override
    public void setFontSize(MPFontSize size) {
        MouseProject.LOGGER.warn("MPGuiScrollPanel cannot support custom FontSize");
    }

    @Override public float getTextScaleMultiplayer() { return 0.0f; }

    @Override
    public void setTextScaleMultiplayer(float multiplayer) {
        MouseProject.LOGGER.warn("MPGuiScrollPanel cannot support custom TextScaleMultiplayer");
    }

    @Override public MPMutableGuiShape getCalculatedInnerShape() { return getCalculatedShape(); }
    @Override public MPGuiPadding getPadding()                   { return MPGuiPadding.ZERO(); }

    @Override
    public void setPadding(MPGuiPadding padding) {
        MouseProject.LOGGER.warn("MPGuiScrollPanel cannot support padding");
    }

    @Override public MPMutableGuiVector getTextOffset() { return new MPMutableGuiVector(); }

    @Override
    public void calculate(IGuiVector pDefSize, IGuiVector pContentSize, IGuiShape available) {
        MPMutableGuiShape calculatedShape = getCalculatedShape();
        MPMutableGuiShape shape           = getShape();
        MPGuiRenderHelper.calculateFlowComponentShape(
                calculatedShape, pDefSize, pContentSize,
                shape, getScaleRules(), available
        );

        if (content != null) {
            MPMutableGuiShape contentAvail = calculatedShape.copy().toMutable();
            boolean           isVert       = orientation == MPOrientation.VERTICAL;

            float scaledSbThickness = isVert
                    ? calculateFlowComponentX(pDefSize, pContentSize, scrollbarThickness)
                    : calculateFlowComponentY(pDefSize, pContentSize, scrollbarThickness);

            if (isVert) {
                contentAvail.withHeight(99999f);
                if (scrollEnabled && scrollbar != null) contentAvail.withWidth(contentAvail.width() - scaledSbThickness);
            } else {
                contentAvail.withWidth(99999f);
                if (scrollEnabled && scrollbar != null) contentAvail.withHeight(contentAvail.height() - scaledSbThickness);
            }

            content.calculate(pDefSize, pContentSize, contentAvail);
            contentSize = isVert ? calculateTrueContentHeight() : calculateTrueContentWidth();

            float viewportSize = isVert ? calculatedShape.height() : calculatedShape.width();
            float maxScroll    = Math.max(0, contentSize - viewportSize);

            if (scrollValue > maxScroll) scrollValue = maxScroll;
            if (scrollValue < 0) scrollValue = 0;

            if (isVert) content.offsetCalculatedShape(0, -scrollValue);
            else content.offsetCalculatedShape(-scrollValue, 0);

            if (scrollEnabled && scrollbar != null) {
                scrollbar.updateSizes(viewportSize, contentSize);
                scrollbar.setScrollValue(scrollValue, false);

                scrollbar.getShape().withX(0).withY(0);
                if (isVert) scrollbar.getShape().withWidth(scrollbarThickness).withHeight(shape.height());
                else scrollbar.getShape().withWidth(shape.width()).withHeight(scrollbarThickness);

                scrollbar.calculate(pDefSize, pContentSize, calculatedShape);

                if (isVert) {
                    float dx = calculatedShape.x() + calculatedShape.width() - scrollbar.getCalculatedShape().width()
                            - scrollbar.getCalculatedShape().x();
                    scrollbar.offsetCalculatedShape(dx, 0);
                } else {
                    float dy = calculatedShape.y() + calculatedShape.height() - scrollbar.getCalculatedShape().height()
                            - scrollbar.getCalculatedShape().y();
                    scrollbar.offsetCalculatedShape(0, dy);
                }
            }
        }
    }

    @Override
    public void offsetCalculatedShape(float dx, float dy) {
        MPGuiElement.super.offsetCalculatedShape(dx, dy);
        if (content != null) content.offsetCalculatedShape(dx, dy);
        if (scrollbar != null) scrollbar.offsetCalculatedShape(dx, dy);
    }

    public void applyScroll(float amount) {
        if (content == null) return;

        float oldScroll = scrollValue;
        scrollValue += amount;

        MPMutableGuiShape calculatedShape = getCalculatedShape();
        boolean           isVert          = orientation == MPOrientation.VERTICAL;
        float             viewportSize    = isVert ? calculatedShape.height() : calculatedShape.width();
        float             maxScroll       = Math.max(0, contentSize - viewportSize);

        if (scrollValue < 0) scrollValue = 0;
        if (scrollValue > maxScroll) scrollValue = maxScroll;

        float diff = scrollValue - oldScroll;
        if (diff != 0) {
            if (isVert) content.offsetCalculatedShape(0, -diff);
            else content.offsetCalculatedShape(-diff, 0);

            if (scrollbar != null) scrollbar.setScrollValue(scrollValue, false);
        }
    }

    private float calculateTrueContentHeight() {
        if (content == null) return 0;
        return Math.max(0, findMaxBottom(content) - content.getCalculatedShape().y());
    }

    private float calculateTrueContentWidth() {
        if (content == null) return 0;
        return Math.max(0, findMaxRight(content) - content.getCalculatedShape().x());
    }

    private float findMaxBottom(MPGuiElement<?> element) {
        if (!element.isVisible()) return element.getCalculatedShape().y();

        float max = element.getCalculatedShape().y() + element.getCalculatedShape().height();
        if (element instanceof MPGuiPanel) {
            for (MPGuiElement<?> child : ((MPGuiPanel<?>) element).getChildren()) {
                max = Math.max(max, findMaxBottom(child));
            }
        }
        return max;
    }

    private float findMaxRight(MPGuiElement<?> element) {
        if (!element.isVisible()) return element.getCalculatedShape().x();

        float max = element.getCalculatedShape().x() + element.getCalculatedShape().width();
        if (element instanceof MPGuiPanel) {
            for (MPGuiElement<?> child : ((MPGuiPanel<?>) element).getChildren()) {
                max = Math.max(max, findMaxRight(child));
            }
        }
        return max;
    }

    //Диспетчеризация событий
    @Override
    public final void dispatchUpdate(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        MPGuiElement.super.dispatchUpdate(mc, mouseX, mouseY, partialTicks);

        MPMutableGuiShape calculatedShape = getCalculatedShape();

        boolean isVert       = orientation == MPOrientation.VERTICAL;
        float   viewportSize = isVert ? calculatedShape.height() : calculatedShape.width();

        if (scrollEnabled && scrollbar != null && contentSize > viewportSize) {
            scrollbar.dispatchUpdate(mc, mouseX, mouseY, partialTicks);
        }
        if (content != null) content.dispatchUpdate(mc, mouseX, mouseY, partialTicks);
    }

    @Override
    public final void dispatchProcessHover(Minecraft mc, int mouseX, int mouseY) {
        MPGuiElement.super.dispatchProcessHover(mc, mouseX, mouseY);
        if (!isVisible()) return;

        MPMutableGuiShape calculatedShape = getCalculatedShape();
        boolean           isVert          = orientation == MPOrientation.VERTICAL;
        float             viewportSize    = isVert ? calculatedShape.height() : calculatedShape.width();

        if (scrollEnabled && scrollbar != null && contentSize > viewportSize) scrollbar.dispatchProcessHover(mc, mouseX, mouseY);
        if (content != null) content.dispatchProcessHover(mc, mouseX, mouseY);

        boolean isHovered = calculatedShape.contains(mouseX, mouseY);
        if (isHovered && !getStateManager().has(MPGuiElementState.HOVERED)) dispatchMouseEnter(mc, mouseX, mouseY);
        else if (!isHovered && getStateManager().has(MPGuiElementState.HOVERED)) dispatchMouseLeave(mc, mouseX, mouseY);
    }

    @Override
    public final boolean dispatchMousePressed(Minecraft mc, int mouseX, int mouseY, int mouseButton) {
        MPMutableGuiShape calculatedShape = getCalculatedShape();
        if (!calculatedShape.contains(mouseX, mouseY)) return false;

        boolean isVert       = orientation == MPOrientation.VERTICAL;
        float   viewportSize = isVert ? calculatedShape.height() : calculatedShape.width();

        if (scrollEnabled && scrollbar != null && contentSize > viewportSize) {
            if (scrollbar.dispatchMousePressed(mc, mouseX, mouseY, mouseButton)) return true;
        }

        if (content != null && content.mouseHover(mc, mouseX, mouseY)) {
            if (content.dispatchMousePressed(mc, mouseX, mouseY, mouseButton)) return true;
        }

        if (!isEnabled() || !isVisible()) {
            dispatchPlaySound(mc, mc.getSoundHandler(), MPSoundSourceType.DISABLED);
            return false;
        }

        if (getStateManager().has(MPGuiElementState.FAIL)) dispatchPlaySound(mc, mc.getSoundHandler(), MPSoundSourceType.FAIL);

        getCore().setTickDown(0);
        getStateManager().add(MPGuiElementState.PRESSED);
        MPGuiMouseClickEvent<T> pressEvent = getCore().getPressEvent();
        MPGuiEventFactory.pushMouseClickEvent(pressEvent, mouseX, mouseY);
        onAnyEventFire(pressEvent);
        if (!pressEvent.isCancelled()) {
            dispatchPlaySound(mc, mc.getSoundHandler(), MPSoundSourceType.PRESS);
            onMousePressed(pressEvent);
        }
        return true;
    }

    @Override
    public final void dispatchMouseReleased(Minecraft mc, int mouseX, int mouseY, int state) {
        getCore().setTickDown(-1);
        getStateManager().remove(MPGuiElementState.PRESSED);

        if (scrollEnabled && scrollbar != null) scrollbar.dispatchMouseReleased(mc, mouseX, mouseY, state);
        if (content != null) content.dispatchMouseReleased(mc, mouseX, mouseY, state);

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
    public final boolean dispatchMouseDragged(Minecraft mc, int mouseX, int mouseY, MPMoveDirection direction, int diffX, int diffY) {
        MPGuiElementCore<T> core            = getCore();
        boolean             handled         = false;
        boolean             isVert          = orientation == MPOrientation.VERTICAL;
        MPMutableGuiShape   calculatedShape = core.getCalculatedShape();
        float               viewportSize    = isVert ? calculatedShape.height() : calculatedShape.width();

        if (scrollEnabled && scrollbar != null && contentSize > viewportSize) {
            if (scrollbar.dispatchMouseDragged(mc, mouseX, mouseY, direction, diffX, diffY)) handled = true;
        }
        if (!handled && content != null) {
            handled = content.dispatchMouseDragged(mc, mouseX, mouseY, direction, diffX, diffY);
        }

        int                    tickDown  = core.getTickDown();
        MPGuiMouseDragEvent<T> dragEvent = core.getDragEvent();
        if (!handled && tickDown >= 0) {
            MPGuiEventFactory.pushMouseDragEvent(dragEvent, mouseX, mouseY, direction, diffX, diffY, tickDown);
            onAnyEventFire(dragEvent);
            if (!dragEvent.isCancelled()) {
                dispatchPlaySound(mc, mc.getSoundHandler(), MPSoundSourceType.DRAG);
                onMouseDragged(dragEvent);
            }
            return !dragEvent.isCancelled();
        }
        return handled;
    }

    @Override
    public final boolean dispatchMouseScrolled(Minecraft mc, int mouseX, int mouseY, int scroll) {
        MPGuiElementCore<T> core = getCore();
        if (!core.getCalculatedShape().contains(mouseX, mouseY)) return false;

        if (content != null && content.dispatchMouseScrolled(mc, mouseX, mouseY, scroll)) return true;

        if (scrollEnabled) {
            float oldScroll = scrollValue;
            applyScroll(-scroll / 10f);
            MPGuiMouseScrollEvent<T> scrollEvent = core.getScrollEvent();
            MPGuiEventFactory.pushMouseScrollEvent(scrollEvent, mouseX, mouseY, MPScrollDirection.getScrollDirection(scroll), scroll);
            onAnyEventFire(scrollEvent);
            if (!scrollEvent.isCancelled()) {
                dispatchPlaySound(mc, mc.getSoundHandler(), MPSoundSourceType.SCROLL);
                onMouseScrolled(scrollEvent);
            }
            return Float.compare(oldScroll, scrollValue) != 0 || scrollEvent.isConsumed();
        }
        return false;
    }

    @Override
    public final boolean dispatchKeyTyped(Minecraft mc, int mouseX, int mouseY, char typedChar, int keyCode) {
        if (!isVisible()) return false;

        if (content != null && content.dispatchKeyTyped(mc, mouseX, mouseY, typedChar, keyCode)) return true;

        MPGuiElementCore<T> core = getCore();

        if (core.getStateManager().has(MPGuiElementState.FOCUSED)) {
            MPGuiKeyEvent<T> keyEvent = core.getKeyEvent();
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
        MPGuiElementCore<T> core        = getCore();
        MPGuiTickEvent<T>   drawBGEvent = core.getDrawBGEvent();
        MPGuiEventFactory.pushTickEvent(drawBGEvent, mouseX, mouseY, partialTicks);
        onAnyEventFire(drawBGEvent);
        if (!drawBGEvent.isCancelled()) {
            onDrawBackground(drawBGEvent);

            if (content != null) {
                setupScissor(mc);
                content.dispatchDrawBackground(mc, mouseX, mouseY, partialTicks);
                restoreScissor();
            }

            boolean           isVert          = orientation == MPOrientation.VERTICAL;
            MPMutableGuiShape calculatedShape = core.getCalculatedShape();
            float             viewportSize    = isVert ? calculatedShape.height() : calculatedShape.width();

            if (scrollEnabled && scrollbar != null && contentSize > viewportSize) {
                scrollbar.dispatchDrawBackground(mc, mouseX, mouseY, partialTicks);
            }
        }
    }

    @Override
    public final void dispatchDrawForeground(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        MPGuiElementCore<T> core        = getCore();
        MPGuiTickEvent<T>   drawFGEvent = core.getDrawFGEvent();
        MPGuiEventFactory.pushTickEvent(drawFGEvent, mouseX, mouseY, partialTicks);
        onAnyEventFire(drawFGEvent);
        if (!drawFGEvent.isCancelled()) {
            onDrawForeground(drawFGEvent);

            if (content != null) {
                setupScissor(mc);
                content.dispatchDrawForeground(mc, mouseX, mouseY, partialTicks);
                restoreScissor();
            }

            boolean           isVert          = orientation == MPOrientation.VERTICAL;
            MPMutableGuiShape calculatedShape = core.getCalculatedShape();
            float             viewportSize    = isVert ? calculatedShape.height() : calculatedShape.width();

            if (scrollEnabled && scrollbar != null && contentSize > viewportSize) {
                scrollbar.dispatchDrawForeground(mc, mouseX, mouseY, partialTicks);
            }
        }
    }

    @Override
    public final void dispatchDrawText(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        MPGuiElementCore<T> core          = getCore();
        MPGuiTickEvent<T>   drawTextEvent = core.getDrawTextEvent();
        MPGuiEventFactory.pushTickEvent(drawTextEvent, mouseX, mouseY, partialTicks);
        onAnyEventFire(drawTextEvent);
        if (!drawTextEvent.isCancelled()) {
            onDrawText(drawTextEvent);

            if (content != null) {
                setupScissor(mc);
                content.dispatchDrawText(mc, mouseX, mouseY, partialTicks);
                restoreScissor();
            }

            boolean           isVert          = orientation == MPOrientation.VERTICAL;
            MPMutableGuiShape calculatedShape = core.getCalculatedShape();
            float             viewportSize    = isVert ? calculatedShape.height() : calculatedShape.width();

            if (scrollEnabled && scrollbar != null && contentSize > viewportSize) {
                scrollbar.dispatchDrawText(mc, mouseX, mouseY, partialTicks);
            }
        }
    }

    @Override
    public final void dispatchDrawLast(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        MPGuiElementCore<T> core          = getCore();
        MPGuiTickEvent<T>   drawLastEvent = core.getDrawLastEvent();
        MPGuiEventFactory.pushTickEvent(drawLastEvent, mouseX, mouseY, partialTicks);
        onAnyEventFire(drawLastEvent);
        if (!drawLastEvent.isCancelled()) {
            onDrawLast(drawLastEvent);

            if (content != null) {
                setupScissor(mc);
                content.dispatchDrawLast(mc, mouseX, mouseY, partialTicks);
                restoreScissor();
            }

            boolean           isVert          = orientation == MPOrientation.VERTICAL;
            MPMutableGuiShape calculatedShape = core.getCalculatedShape();
            float             viewportSize    = isVert ? calculatedShape.height() : calculatedShape.width();

            if (scrollEnabled && scrollbar != null && contentSize > viewportSize) {
                scrollbar.dispatchDrawLast(mc, mouseX, mouseY, partialTicks);
            }
        }
    }

    private void setupScissor(Minecraft mc) {
        int scale    = new ScaledResolution(mc).getScaleFactor();
        int scissorX = (int) (getCalculatedShape().x() * scale);
        int scissorY = (int) (mc.displayHeight - (getCalculatedShape().y() + getCalculatedShape().height()) * scale);
        int scissorW = (int) (getCalculatedShape().width() * scale);
        int scissorH = (int) (getCalculatedShape().height() * scale);

        MPGuiRenderHelper.pushScissor(scissorX, scissorY, scissorW, scissorH, scissorState);
    }

    private void restoreScissor() { MPGuiRenderHelper.popScissor(scissorState); }

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


}