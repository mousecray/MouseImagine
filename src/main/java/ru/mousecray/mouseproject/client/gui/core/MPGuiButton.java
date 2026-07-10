/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mouseproject.client.gui.core;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import ru.mousecray.mouseproject.MouseProject;
import ru.mousecray.mouseproject.client.gui.core.component.color.MPGuiColorPack;
import ru.mousecray.mouseproject.client.gui.core.component.lang.MPGuiString;
import ru.mousecray.mouseproject.client.gui.core.component.sound.MPGuiSoundPack;
import ru.mousecray.mouseproject.client.gui.core.component.state.MPGuiElementState;
import ru.mousecray.mouseproject.client.gui.core.dim.IGuiShape;
import ru.mousecray.mouseproject.client.gui.core.dim.MPGuiShape;
import ru.mousecray.mouseproject.client.gui.core.event.MPGuiKeyEvent;
import ru.mousecray.mouseproject.client.gui.core.event.MPGuiMouseClickEvent;
import ru.mousecray.mouseproject.client.gui.core.event.MPGuiMouseMoveEvent;
import ru.mousecray.mouseproject.client.gui.core.misc.MPMoveDirection;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class MPGuiButton<T extends MPGuiButton<T>> extends GuiButton implements MPGuiElement<T> {
    protected final MPGuiElementCore<T> core;

    public MPGuiButton(MPGuiShape shape) {
        super(0,
                (int) shape.x(), (int) shape.y(),
                (int) shape.width(), (int) shape.height(),
                "");

        core = new MPGuiElementCore<>(shape);
        core.bindEvents(Minecraft.getMinecraft(), self());

        setColorPack(MPGuiColorPack.CONTROL_SIMPLE());
        setSoundPack(MPGuiSoundPack.CONTROL_SIMPLE());

        getStateManager().setChangeListener(() -> {
            enabled = !getStateManager().has(MPGuiElementState.DISABLED);
            visible = !getStateManager().has(MPGuiElementState.HIDDEN);
            hovered = getStateManager().has(MPGuiElementState.HOVERED);
        });
    }

    @SuppressWarnings("unchecked") @Override public T self() { return (T) this; }
    @Override public MPGuiElementCore<T> getCore()           { return core; }
    @Override public void setId(int id)                      { this.id = id; }
    @Override public int getId()                             { return id; }

    @Override
    public void setGuiString(MPGuiString guiString) {
        MPGuiElement.super.setGuiString(guiString);
        displayString = guiString.get();
    }

    @Override
    public void setupShapeToVanilla(IGuiShape result) {
        x = (int) result.x();
        y = (int) result.y();
        width = (int) result.width();
        height = (int) result.height();
    }

    @Override
    public boolean onForgeClickIntegrationPre() {
        @SuppressWarnings("DataFlowIssue")
        GuiScreenEvent.ActionPerformedEvent.Pre forgeEvent =
                new GuiScreenEvent.ActionPerformedEvent.Pre(getScreen(), this, getScreen().getButtonList());
        return MinecraftForge.EVENT_BUS.post(forgeEvent);
    }

    @Override
    public void onForgeClickIntegrationPost() {
        MinecraftForge.EVENT_BUS.post(
                new GuiScreenEvent.ActionPerformedEvent.Post(
                        getScreen(), this,
                        getScreen() == null ? new ArrayList<>() : getScreen().getButtonList()
                )
        );
    }

    @Override
    public void onKeyTyped(MPGuiKeyEvent<T> event) {
        if (!event.isCancelled() && (event.getKeyCode() == Keyboard.KEY_RETURN || event.getKeyCode() == Keyboard.KEY_NUMPADENTER)) {
            dispatchMousePressed(event.getMc(), x + width / 2, y + height / 2, 0);
            dispatchMouseReleased(event.getMc(), x + width / 2, y + height / 2, 0);
            event.consume();
        }
    }

    @Override public abstract void onClick(MPGuiMouseClickEvent<T> event);

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
    public void setWidth(int width) {
        MouseProject.LOGGER.warn(
                "Width cannot be setup directly to MPGuiElement." +
                        " It set now, but actual element size will be updated on the next gui size calculation."
        );
        this.width = width;
    }

    @Override
    public final void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        dispatchDraw(mc, mouseX, mouseY, partialTicks);
    }

    @Override
    public void drawButtonForegroundLayer(int mouseX, int mouseY) {
        dispatchDrawForeground(Minecraft.getMinecraft(), mouseX, mouseY, Minecraft.getMinecraft().getRenderPartialTicks());
    }

    @Override
    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
        if (getCore().getTickDown() >= 0) {
            MPGuiMouseMoveEvent<T> moveEvent = getCore().getMoveEvent();
            int                    diffX     = mouseX - moveEvent.getMouseX();
            int                    diffY     = mouseY - moveEvent.getMouseY();
            MPMoveDirection        direction = MPMoveDirection.getMoveDirection(diffX, diffY);
            if (direction != null) dispatchMouseDragged(mc, mouseX, mouseY, direction, diffX, diffY);
        }
    }

    @Override public int getPackedFGColour() { return packedFGColour; }
}