package ru.mousecray.realdream.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.SoundEvent;
import org.lwjgl.opengl.GL11;
import ru.mousecray.realdream.client.gui.container.RDGuiPanel;
import ru.mousecray.realdream.client.gui.dim.GuiPadding;
import ru.mousecray.realdream.client.gui.dim.GuiScaleRules;
import ru.mousecray.realdream.client.gui.dim.GuiScaleType;
import ru.mousecray.realdream.client.gui.dim.GuiShape;
import ru.mousecray.realdream.client.gui.event.*;
import ru.mousecray.realdream.client.gui.state.GuiButtonActionState;
import ru.mousecray.realdream.client.gui.state.GuiButtonPersistentState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

public abstract class RDGuiLabel<T extends RDGuiLabel<T>> extends GuiLabel implements RDGuiElement<T> {
    private final RDGuiTickEvent<T>
            updateEvent   = new RDGuiTickEvent<>(),
            drawBGEvent   = new RDGuiTickEvent<>(),
            drawFGEvent   = new RDGuiTickEvent<>(),
            drawLastEvent = new RDGuiTickEvent<>(),
            drawTextEvent = new RDGuiTickEvent<>();
    private final RDGuiMouseClickEvent<T>
            pressEvent   = new RDGuiMouseClickEvent<>(RDClickType.PRESS),
            releaseEvent = new RDGuiMouseClickEvent<>(RDClickType.RELEASE),
            clickEvent   = new RDGuiMouseClickEvent<>(RDClickType.CLICK);
    private final RDGuiMouseMoveEvent<T> moveEvent  = new RDGuiMouseMoveEvent<>();
    private final RDGuiMouseDragEvent<T> dragEvent  = new RDGuiMouseDragEvent<>();
    private final RDGuiSoundEvent<T>     soundEvent = new RDGuiSoundEvent<>();

    private final       FontRenderer             fontRenderer;
    protected final     RDFontSize               fontSize;
    protected           float                    textScaleMultiplayer = 1.0F;
    private             boolean                  centered;
    @Nullable protected GuiButtonActionState     actionState          = null;
    @Nullable protected GuiButtonPersistentState persistentState      = GuiButtonPersistentState.NORMAL;
    protected           StateColorContainer      colorContainer       = StateColorContainer.createDefault();
    private             int                      tickDown             = -1;
    private             int                      partialTick;
    private             boolean                  hovered;

    private final GuiShape elementShape;
    private       GuiShape calculatedElementShape;

    private GuiScaleRules scaleRules = new GuiScaleRules(GuiScaleType.FLOW);

    private RDGuiPanel<?> parent;
    private GuiPadding    padding = new GuiPadding(0);

    private RDGuiScreen screen;

    @Nullable private final SoundEvent soundClick;

    public RDGuiLabel(String text, FontRenderer fontRenderer, GuiShape elementShape, int color, RDFontSize fontSize, @Nullable SoundEvent soundClick) {
        super(
                fontRenderer, 0,
                (int) elementShape.getX(), (int) elementShape.getY(),
                (int) elementShape.getWidth(), (int) elementShape.getHeight(),
                color
        );
        this.fontRenderer = fontRenderer;
        this.elementShape = elementShape;
        this.soundClick = soundClick;
        this.fontSize = fontSize;

        addLine(text);
    }

    @Override public GuiShape getElementShape()           { return elementShape; }
    @Override public GuiShape getCalculatedElementShape() { return calculatedElementShape; }
    @Override public String getText()                     { return String.join("\n", labels); }


    @Override
    public void setText(String rawText) {
        labels.clear();
        String[] split = rawText.split("\n");
        labels.addAll(Arrays.asList(split));
    }

    @Override public void setId(int id)                                      { this.id = id; }
    @Override public int getId()                                             { return id; }

    @Override @SuppressWarnings("unchecked") public T self()                 { return (T) this; }

    @Override @Nullable public GuiButtonActionState getActionState()         { return actionState; }
    @Override @Nullable public GuiButtonPersistentState getPersistentState() { return persistentState; }
    @Override public GuiTexturePack getTexturePack()                         { return GuiTexturePack.EMPTY; }

    @Override
    public boolean applyState(@Nullable GuiButtonPersistentState state) {
        processVanillaPersistentState(state);
        persistentState = state;
        return true;
    }

    protected boolean applyActionState(@Nullable GuiButtonActionState state) {
        processVanillaActionState(state);
        actionState = state;
        return true;
    }

    private void processVanillaActionState(@Nullable GuiButtonActionState state) { }

    private void processVanillaPersistentState(@Nullable GuiButtonPersistentState state) {
        visible = state != null;
    }

    @Override protected final void onUpdate0(Minecraft mc, int mouseX, int mouseY) {
        if (++partialTick >= 20) partialTick = 0;
        if (tickDown >= 0) ++tickDown;

        RDGuiEventFactory.pushTickEvent(updateEvent, self(), mc, mouseX, mouseY, partialTick);
        onAnyEventFire(updateEvent);
        if (!updateEvent.isCancelled()) onUpdate(updateEvent);
        int           diffX     = mouseX - moveEvent.getMouseX();
        int           diffY     = mouseY - moveEvent.getMouseY();
        MoveDirection direction = MoveDirection.getMoveDirection(diffX, diffY);
        RDGuiEventFactory.pushMouseMoveEvent(moveEvent, self(), mc, mouseX, mouseY, direction);
        if (tickDown >= 0 && direction != null) {
            RDGuiEventFactory.pushMouseDragEvent(dragEvent, self(), mc, mouseX, mouseY, direction, diffX, diffY, tickDown);
            onAnyEventFire(dragEvent);
            if (!dragEvent.isCancelled()) onMouseDragged(dragEvent);
        }
    }

    @Override protected final void onMouseEnter0(Minecraft mc, int mouseX, int mouseY) {
        onAnyEventFire(moveEvent);
        if (!moveEvent.isCancelled()) {
            if (persistentState == null
                    || persistentState == GuiButtonPersistentState.DISABLED
                    || actionState == GuiButtonActionState.PRESSED) return;
            hovered = true;
            applyActionState(GuiButtonActionState.HOVER);
            onMouseEnter(moveEvent);
        }
    }

    @Override protected final void onMouseLeave0(Minecraft mc, int mouseX, int mouseY) {
        onAnyEventFire(moveEvent);
        if (!moveEvent.isCancelled()) {
            if (actionState == GuiButtonActionState.HOVER) applyActionState(null);
            hovered = false;
            onMouseLeave(moveEvent);
        }
    }

    @Override protected final void onMouseReleased0(Minecraft mc, int mouseX, int mouseY) {
        RDGuiEventFactory.pushMouseClickEvent(releaseEvent, self(), mc, mouseX, mouseY);
        onAnyEventFire(releaseEvent);
        if (!releaseEvent.isCancelled()) {
            if (isMouseOver()) applyActionState(GuiButtonActionState.HOVER);
            else applyActionState(null);
            tickDown = -1;
            if (persistentState != null) {
                onMouseReleased(releaseEvent);
                if (isMouseOver()) {
                    RDGuiEventFactory.pushMouseClickEvent(clickEvent, self(), mc, mouseX, mouseY);
                    onAnyEventFire(clickEvent);
                    if (!clickEvent.isCancelled()) onClick(clickEvent);
                }
            }
        }
    }

    @Override protected final void onMousePressed0(Minecraft mc, int mouseX, int mouseY) {
        RDGuiEventFactory.pushMouseClickEvent(pressEvent, self(), mc, mouseX, mouseY);
        onAnyEventFire(pressEvent);
        if (!pressEvent.isCancelled()) {
            if (persistentState == null || persistentState == GuiButtonPersistentState.DISABLED) return;
            applyActionState(GuiButtonActionState.PRESSED);
            tickDown = 0;
            onMousePressed(pressEvent);
            onPlaySound0(mc, mc.getSoundHandler(), soundClick, SoundSourceType.PRESS);
        }
    }

    protected final void onPlaySound0(Minecraft mc, SoundHandler soundHandler, @Nullable SoundEvent sound, SoundSourceType source) {
        if (sound != null) {
            RDGuiEventFactory.pushSoundEvent(soundEvent, self(), mc, moveEvent.getMouseX(), moveEvent.getMouseY(), soundHandler, sound, source);
            onAnyEventFire(soundEvent);
            if (!soundEvent.isCancelled()) onPlaySound(soundEvent);
        }
    }

    protected void onPlaySound(RDGuiSoundEvent<T> event) {
        event.getHandler().playSound(PositionedSoundRecord.getMasterRecord(event.getSound(), 1.0F));
    }

    public void mouseReleased(int mouseX, int mouseY) { onMouseReleased0(Minecraft.getMinecraft(), mouseX, mouseY); }

    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
        if (tickDown >= 0) {
            int           diffX     = mouseX - moveEvent.getMouseX();
            int           diffY     = mouseY - moveEvent.getMouseY();
            MoveDirection direction = MoveDirection.getMoveDirection(diffX, diffY);
            if (direction != null) {
                RDGuiEventFactory.pushMouseDragEvent(dragEvent, self(), mc, mouseX, mouseY, direction, diffX, diffY, tickDown);
                onAnyEventFire(dragEvent);
                if (!dragEvent.isCancelled()) onMouseDragged(dragEvent);
            }
        }
    }

    protected void onAnyEventFire(RDGuiEvent<T> event) { }

    @Override public boolean mouseHover(Minecraft mc, int mouseX, int mouseY) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }

    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }

    protected abstract void onClick(RDGuiMouseClickEvent<T> event);
    protected void onDrag(RDGuiMouseDragEvent<T> event) { }

    protected void onUpdate(RDGuiTickEvent<T> event)    { }
    protected void onMouseDragged(RDGuiMouseDragEvent<T> event) {
        if (!event.isCancelled()) onDrag(event);
    }
    protected void onMouseReleased(RDGuiMouseClickEvent<T> event) { }
    protected void onMouseEnter(RDGuiMouseMoveEvent<T> event)     { }
    protected void onMouseLeave(RDGuiMouseMoveEvent<T> event)     { }
    protected void onMousePressed(RDGuiMouseClickEvent<T> event)  { }

    protected final int getHoverState(boolean mouseOver) {
        return persistentState == GuiButtonPersistentState.DISABLED ? 0 : mouseOver ? 2 : 1;
    }

    public final boolean isMouseOver() { return hovered; }

    public final void playPressSound(SoundHandler soundHandler) {
        onPlaySound0(Minecraft.getMinecraft(), Minecraft.getMinecraft().getSoundHandler(), soundClick, SoundSourceType.PRESS);
    }

    protected final void onDrawLabelBackground(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        RDGuiEventFactory.pushTickEvent(drawBGEvent, self(), mc, mouseX, mouseY, partialTicks);
        onAnyEventFire(drawBGEvent);
        if (persistentState != null && !drawBGEvent.isCancelled()) drawLabelBackgroundLayer(drawBGEvent);
    }

    protected final void onDrawLabelForeground(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        RDGuiEventFactory.pushTickEvent(drawFGEvent, self(), mc, mouseX, mouseY, partialTicks);
        onAnyEventFire(drawFGEvent);
        if (persistentState != null && !drawFGEvent.isCancelled()) drawLabelForegroundLayer(drawFGEvent);
    }

    protected final void onDrawLabelText(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        RDGuiEventFactory.pushTickEvent(drawTextEvent, self(), mc, mouseX, mouseY, partialTicks);
        onAnyEventFire(drawTextEvent);
        if (persistentState != null && !drawTextEvent.isCancelled()) drawLabelTextLayer(drawTextEvent);
    }

    protected final void onDrawLabelLast(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        RDGuiEventFactory.pushTickEvent(drawLastEvent, self(), mc, mouseX, mouseY, partialTicks);
        onAnyEventFire(drawLastEvent);
        if (persistentState != null && !drawLastEvent.isCancelled()) drawLabelLastLayer(drawLastEvent);
    }

    protected final void onDrawLabel(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        onDrawLabelBackground(mc, mouseX, mouseY, partialTicks);
        onDrawLabelForeground(mc, mouseX, mouseY, partialTicks);
        onDrawLabelText(mc, mouseX, mouseY, partialTicks);
        onDrawLabelLast(mc, mouseX, mouseY, partialTicks);
    }

    protected void drawLabelLastLayer(RDGuiTickEvent<T> event)       { }
    protected void drawLabelForegroundLayer(RDGuiTickEvent<T> event) { }
    protected void drawLabelBackgroundLayer(RDGuiTickEvent<T> event) {
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        drawLabelBackground(event.getMc(), event.getMouseX(), event.getMouseY());
    }

    protected void drawLabelTextLayer(RDGuiTickEvent<T> event) {
        if (!visible) return;

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        int color = colorContainer.getCalculatedColor(actionState, persistentState, 0);

        float scale        = 1.0F;
        float inverseScale = 1.0F;
        if (fontSize != RDFontSize.NORMAL) {
            scale = fontSize.getScale();
            inverseScale = 1.0F / scale;
        }

        float i = y * inverseScale + height * inverseScale / 2f * inverseScale;
        float j = i - labels.size() * (fontRenderer.FONT_HEIGHT * inverseScale + 1) / 2f * inverseScale;

        GlStateManager.pushMatrix();
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

        GlStateManager.scale(scale, scale, 1.0F);
        for (int k = 0; k < labels.size(); ++k) {
            if (centered) {
                GuiRenderHelper.drawCenteredString(fontRenderer, labels.get(k), x * inverseScale + width * inverseScale / 2f * inverseScale, j + k * ((fontRenderer.FONT_HEIGHT * inverseScale) + 1), color, true);
            } else {
                GuiRenderHelper.drawString(fontRenderer, labels.get(k), x * inverseScale, j + k * ((fontRenderer.FONT_HEIGHT * inverseScale) + 1), color, true);
            }
        }
        GlStateManager.popMatrix();
    }

    @Nonnull @Override
    public GuiLabel setCentered() {
        centered = true;
        return super.setCentered();
    }

    @Override
    public void drawLabel(@Nonnull Minecraft mc, int mouseX, int mouseY) {
        onDrawLabel(mc, mouseX, mouseY, Minecraft.getMinecraft().getRenderPartialTicks());
    }
}