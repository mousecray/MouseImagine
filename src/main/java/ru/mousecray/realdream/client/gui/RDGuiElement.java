package ru.mousecray.realdream.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.realdream.client.gui.container.RDGuiPanel;
import ru.mousecray.realdream.client.gui.dim.*;
import ru.mousecray.realdream.client.gui.misc.MoveDirection;
import ru.mousecray.realdream.client.gui.misc.lang.RDGuiString;
import ru.mousecray.realdream.client.gui.misc.texture.RDGuiTexturePack;
import ru.mousecray.realdream.client.gui.state.GuiButtonActionState;
import ru.mousecray.realdream.client.gui.state.GuiButtonPersistentState;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public interface RDGuiElement<T extends RDGuiElement<T>> {
    T self();
    MutableGuiShape getElementShape();
    MutableGuiShape getCalculatedElementShape();
    void calculate(IGuiVector parentDefaultSize, IGuiVector parentContentSize, IGuiShape available);
    GuiScaleRules getScaleRules();
    void setId(int id);
    int getId();
    String getText();
    void setText(String text);
    void setGuiString(RDGuiString guiString);
    RDGuiString getGuiString();
    boolean applyState(@Nullable GuiButtonPersistentState state);
    void setElementShape(IGuiShape elementShape);
    RDGuiTexturePack getTexturePack();
    void setTexturePack(RDGuiTexturePack texturePack);
    @Nullable GuiButtonActionState getActionState();
    @Nullable GuiButtonPersistentState getPersistentState();
    void onUpdate0(Minecraft mc, int mouseX, int mouseY);
    void onMouseEnter0(Minecraft mc, int mouseX, int mouseY);
    void onMouseLeave0(Minecraft mc, int mouseX, int mouseY);
    void onMousePressed0(Minecraft mc, int mouseX, int mouseY);
    void onMouseReleased0(Minecraft mc, int mouseX, int mouseY);
    void onMouseDragged0(Minecraft mc, int mouseX, int mouseY, MoveDirection direction, int diffX, int diffY);
    boolean mouseHover(Minecraft mc, int mouseX, int mouseY);
    void onDrawBackground(Minecraft mc, int mouseX, int mouseY, float partialTicks);
    void onDrawForeground(Minecraft mc, int mouseX, int mouseY, float partialTicks);
    void onDrawText(Minecraft mc, int mouseX, int mouseY, float partialTicks);
    void onDrawLast(Minecraft mc, int mouseX, int mouseY, float partialTicks);
    RDGuiElement<?> findTopHovered(Minecraft mc, int mouseX, int mouseY);
    RDGuiScreen getScreen();
    void setScreen(RDGuiScreen screen);
    RDGuiPanel<?> getParent();
    void setParent(RDGuiPanel<?> parent);
    GuiMargin getPadding();
    void setPadding(GuiPadding padding);
    void setTextOffset(IGuiVector offset);
    MutableGuiVector getTextOffset();
    void setScaleRules(GuiScaleRules scaleRules);
    void measurePreferred(IGuiVector parentDefaultSize, IGuiVector parentContentSize, float suggestedX, float suggestedY, MutableGuiVector result);
    void offsetCalculatedShape(float dx, float dy);
}