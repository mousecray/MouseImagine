package ru.mousecray.realdream.client.gui.container;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.realdream.client.gui.RDGuiElement;
import ru.mousecray.realdream.client.gui.RDGuiScreen;
import ru.mousecray.realdream.client.gui.RDGuiTextField;
import ru.mousecray.realdream.client.gui.dim.*;
import ru.mousecray.realdream.client.gui.misc.MoveDirection;
import ru.mousecray.realdream.client.gui.misc.lang.RDGuiString;
import ru.mousecray.realdream.client.gui.misc.texture.RDGuiTexture;
import ru.mousecray.realdream.client.gui.misc.texture.RDGuiTexturePack;
import ru.mousecray.realdream.client.gui.state.GuiButtonActionState;
import ru.mousecray.realdream.client.gui.state.GuiButtonPersistentState;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.mousecray.realdream.client.gui.misc.GuiRenderHelper.*;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class RDGuiPanel<T extends RDGuiPanel<T>> implements RDGuiElement<T> {
    protected final List<RDGuiElement<?>> children = new ArrayList<>();

    public List<RDGuiElement<?>> getChildren() { return children; }

    private final Map<RDGuiElement<?>, GuiMargin>      childMargins = new HashMap<>();
    private final Map<RDGuiElement<?>, AnchorPosition> childAnchors = new HashMap<>();
    private final Map<RDGuiElement<?>, GuiVector>      childOffsets = new HashMap<>();

    private GuiPadding    padding    = new GuiPadding(0);
    private LayoutType    layoutType = LayoutType.FREE;
    private GuiScaleRules scaleRules = new GuiScaleRules(GuiScaleType.FLOW);

    private final MutableGuiShape elementShape, calculatedElementShape = new MutableGuiShape();
    private final MutableGuiVector measureTemp        = new MutableGuiVector();
    private final MutableGuiShape  childAvailableTemp = new MutableGuiShape();
    private final float[]          marginTemp         = new float[4];

    private RDGuiScreen   screen;
    private RDGuiPanel<?> parent;

    private RDGuiTexturePack texturePack = RDGuiTexturePack.EMPTY;
    private int              id;

    public RDGuiPanel(GuiShape elementShape) {
        this.elementShape = elementShape.toMutable();
    }

    @Override public void setElementShape(IGuiShape elementShape) { this.elementShape.withShape(elementShape); }

    @SuppressWarnings("unchecked") @Override public T self()      { return (T) this; }

    @Override public void setPadding(GuiPadding padding)          { this.padding = padding; }
    @Override public GuiPadding getPadding()                      { return padding; }
    public void setLayoutType(LayoutType layoutType)              { this.layoutType = layoutType; }
    public LayoutType getLayoutType()                             { return layoutType; }

    public void addChild(RDGuiElement<?> child, @Nullable GuiMargin margin, @Nullable AnchorPosition anchorPosition, @Nullable GuiVector offset) {
        children.add(child);
        childMargins.put(child, margin != null ? margin : new GuiMargin(0));

        if (layoutType == LayoutType.ANCHOR) {
            childAnchors.put(child, anchorPosition);
            childOffsets.put(child, offset != null ? offset : GuiVector.ZERO);
        }

        child.setParent(self());
        if (screen != null) {
            child.setScreen(screen);
            child.setId(screen.genNextElementID());
        }
    }

    @Override public void setId(int id) { this.id = id; }

    @Override
    public void setScreen(RDGuiScreen screen) {
        this.screen = screen;
        for (RDGuiElement<?> child : children) child.setScreen(screen);
    }

    @Override public RDGuiScreen getScreen()                                      { return screen; }
    @Override public RDGuiPanel<?> getParent()                                    { return parent; }
    @Override public void setParent(RDGuiPanel<?> parent)                         { this.parent = parent; }
    @Override public GuiScaleRules getScaleRules()                                { return scaleRules; }
    @Override public void setScaleRules(GuiScaleRules scaleRules)                 { this.scaleRules = scaleRules; }
    @Override public RDGuiTexturePack getTexturePack()                            { return texturePack; }
    @Override public void setTexturePack(RDGuiTexturePack texturePack)            { this.texturePack = texturePack; }
    @Override public MutableGuiShape getElementShape()                            { return elementShape; }
    @Override public MutableGuiShape getCalculatedElementShape()                  { return calculatedElementShape; }
    @Override public int getId()                                                  { return id; }
    @Override public String getText()                                             { return ""; }
    @Override public void setText(String text)                                    { }
    @Override public void setGuiString(RDGuiString guiString)                     { }
    @Override public RDGuiString getGuiString()                                   { return RDGuiString.simple(""); }
    @Override public void setTextOffset(IGuiVector offset)                        { }
    @Override public MutableGuiVector getTextOffset()                             { return new MutableGuiVector(); }

    @Override public boolean applyState(@Nullable GuiButtonPersistentState state) { return true; }

    @Override @Nullable public GuiButtonActionState getActionState()              { return null; }
    @Override @Nullable public GuiButtonPersistentState getPersistentState()      { return GuiButtonPersistentState.NORMAL; }

    @Override
    public void calculate(IGuiVector parentDefaultSize, IGuiVector parentContentSize, IGuiShape available) {
        calculateFlowComponentShapeWithPad(parentDefaultSize, parentContentSize, available, calculatedElementShape, elementShape, scaleRules, padding);

        if (calculatedElementShape.width() <= 0 || calculatedElementShape.height() <= 0) return;

        if (layoutType == LayoutType.LINEAR_HORIZONTAL) layoutLinearHorizontal(parentDefaultSize, calculatedElementShape);
        else if (layoutType == LayoutType.LINEAR_VERTICAL) layoutLinearVertical(parentDefaultSize, calculatedElementShape);
        else if (layoutType == LayoutType.ANCHOR) layoutAnchor(parentDefaultSize, calculatedElementShape);
        else layoutFree(parentDefaultSize, calculatedElementShape);
    }

    private void layoutFree(IGuiVector parentDefaultSize, MutableGuiShape inner) {
        for (RDGuiElement<?> child : children) {
            measureChildWithMargin(parentDefaultSize, inner.size(), child, getChildMargin(child), marginTemp, measureTemp);
            float ml = marginTemp[0], mt = marginTemp[1], mr = marginTemp[2], mb = marginTemp[3];

            float childW = measureTemp.x();
            float childH = measureTemp.y();

            float posX = child.getScaleRules().isFixedHorizontal()
                    ? child.getElementShape().x()
                    : calculateFlowComponentX(parentDefaultSize, inner.size(), child.getElementShape().x());

            float posY = child.getScaleRules().isFixedVertical()
                    ? child.getElementShape().y()
                    : calculateFlowComponentY(parentDefaultSize, inner.size(), child.getElementShape().y());

            childAvailableTemp.withX(inner.x() + ml + posX);
            childAvailableTemp.withY(inner.y() + mt + posY);
            childAvailableTemp.withWidth(childW);
            childAvailableTemp.withHeight(childH);

            child.calculate(parentDefaultSize, inner.size(), childAvailableTemp);
        }
    }

    private void layoutLinearHorizontal(IGuiVector parentDefaultSize, MutableGuiShape inner) {
        float fixedSum  = 0f;
        int   fillCount = 0;

        for (RDGuiElement<?> child : children) {
            measureChildWithMargin(parentDefaultSize, inner.size(), child, getChildMargin(child), marginTemp, measureTemp);
            float ml = marginTemp[0], mt = marginTemp[1], mr = marginTemp[2], mb = marginTemp[3];

            float prefW = measureTemp.x();

            if (child.getScaleRules().isParentHorizontal()) fillCount++;
            fixedSum += prefW + ml + mr;
        }

        float remaining = inner.width() - fixedSum;
        float fillW     = fillCount > 0 && remaining > 0 ? remaining / fillCount : 0f;

        float curX = inner.x();
        for (RDGuiElement<?> child : children) {
            measureChildWithMargin(parentDefaultSize, inner.size(), child, getChildMargin(child), marginTemp, measureTemp);
            float ml = marginTemp[0], mt = marginTemp[1], mr = marginTemp[2], mb = marginTemp[3];

            float childAvailH = inner.height() - mt - mb;

            float childW;
            if (child.getScaleRules().isParentHorizontal()) childW = fillW;
            else {
                child.measurePreferred(parentDefaultSize, inner.size(), Float.MAX_VALUE, childAvailH, measureTemp);
                childW = measureTemp.x();
            }

            child.measurePreferred(parentDefaultSize, inner.size(), childW, childAvailH, measureTemp);
            float childH = measureTemp.y();

            childAvailableTemp.withX(curX + ml);
            childAvailableTemp.withY(inner.y() + mt);
            childAvailableTemp.withWidth(childW);
            childAvailableTemp.withHeight(childH);

            child.calculate(parentDefaultSize, inner.size(), childAvailableTemp);

            curX += ml + childW + mr;
        }
    }

    private void layoutLinearVertical(IGuiVector parentDefaultSize, MutableGuiShape inner) {
        float fixedSum  = 0f;
        int   fillCount = 0;

        for (RDGuiElement<?> child : children) {
            measureChildWithMargin(parentDefaultSize, inner.size(), child, getChildMargin(child), marginTemp, measureTemp);
            float ml = marginTemp[0], mt = marginTemp[1], mr = marginTemp[2], mb = marginTemp[3];

            float prefH = measureTemp.y();

            if (child.getScaleRules().isParentVertical()) fillCount++;
            fixedSum += prefH + mt + mb;
        }

        float remaining = inner.height() - fixedSum;
        float fillH     = fillCount > 0 && remaining > 0 ? remaining / fillCount : 0f;

        float curY = inner.y();
        for (RDGuiElement<?> child : children) {
            measureChildWithMargin(parentDefaultSize, inner.size(), child, getChildMargin(child), marginTemp, measureTemp);
            float ml = marginTemp[0], mt = marginTemp[1], mr = marginTemp[2], mb = marginTemp[3];

            float childAvailW = inner.width() - ml - mr;

            float childH;
            if (child.getScaleRules().isParentVertical()) childH = fillH;
            else {
                child.measurePreferred(parentDefaultSize, inner.size(), childAvailW, Float.MAX_VALUE, measureTemp);
                childH = measureTemp.y();
            }

            child.measurePreferred(parentDefaultSize, inner.size(), childAvailW, childH, measureTemp);
            float childW = measureTemp.x();

            childAvailableTemp.withX(inner.x() + ml);
            childAvailableTemp.withY(curY + mt);
            childAvailableTemp.withWidth(childW);
            childAvailableTemp.withHeight(childH);

            child.calculate(parentDefaultSize, inner.size(), childAvailableTemp);

            curY += mt + childH + mb;
        }
    }

    private void layoutAnchor(IGuiVector parentDefaultSize, MutableGuiShape inner) {
        for (RDGuiElement<?> child : children) {
            measureChildWithMargin(parentDefaultSize, inner.size(), child, getChildMargin(child), marginTemp, measureTemp);
            float ml = marginTemp[0], mt = marginTemp[1], mr = marginTemp[2], mb = marginTemp[3];

            float childW = measureTemp.x();
            float childH = measureTemp.y();

            float childX = inner.x() + ml;
            float childY = inner.y() + mt;

            AnchorPosition anchor = childAnchors.get(child);
            if (anchor != null) {
                GuiVector offset  = childOffsets.getOrDefault(child, GuiVector.ZERO);
                float     offsetX = calculateFlowComponentX(parentDefaultSize, inner.size(), offset.x());
                float     offsetY = calculateFlowComponentY(parentDefaultSize, inner.size(), offset.y());

                switch (anchor) {
                    case TOP_LEFT:
                        childX += offsetX;
                        childY += offsetY;
                        break;
                    case TOP_CENTER:
                        childX += (inner.width() - childW) / 2 + offsetX;
                        childY += offsetY;
                        break;
                    case TOP_RIGHT:
                        childX += inner.width() - childW - mr + offsetX;
                        childY += offsetY;
                        break;
                    case MIDDLE_LEFT:
                        childX += offsetX;
                        childY += (inner.height() - childH) / 2 + offsetY;
                        break;
                    case MIDDLE_CENTER:
                        childX += (inner.width() - childW) / 2 + offsetX;
                        childY += (inner.height() - childH) / 2 + offsetY;
                        break;
                    case MIDDLE_RIGHT:
                        childX += inner.width() - childW - mr + offsetX;
                        childY += (inner.height() - childH) / 2 + offsetY;
                        break;
                    case BOTTOM_LEFT:
                        childX += offsetX;
                        childY += inner.height() - childH - mb + offsetY;
                        break;
                    case BOTTOM_CENTER:
                        childX += (inner.width() - childW) / 2 + offsetX;
                        childY += inner.height() - childH - mb + offsetY;
                        break;
                    case BOTTOM_RIGHT:
                        childX += inner.width() - childW - mr + offsetX;
                        childY += inner.height() - childH - mb + offsetY;
                        break;
                }
            }

            childAvailableTemp.withX(childX);
            childAvailableTemp.withY(childY);
            childAvailableTemp.withWidth(childW);
            childAvailableTemp.withHeight(childH);

            child.calculate(parentDefaultSize, inner.size(), childAvailableTemp);
        }
    }

    private GuiMargin getChildMargin(RDGuiElement<?> child) {
        return childMargins.getOrDefault(child, new GuiMargin(0));
    }

    @Override
    public void measurePreferred(IGuiVector parentDefaultSize, IGuiVector parentContentSize, float suggestedX, float suggestedY, MutableGuiVector result) {
        measurePreferredWithScaleRules(parentDefaultSize, parentContentSize, suggestedX, suggestedY, result, elementShape, scaleRules);
    }

    @Override
    public void onUpdate0(Minecraft mc, int mouseX, int mouseY) {
        for (RDGuiElement<?> child : children) child.onUpdate0(mc, mouseX, mouseY);
    }

    @Override
    public void onMouseEnter0(Minecraft mc, int mouseX, int mouseY) {
        RDGuiElement<?> hovered = findTopHovered(mc, mouseX, mouseY);
        if (hovered != null) hovered.onMouseEnter0(mc, mouseX, mouseY);
    }

    @Override
    public void onMouseLeave0(Minecraft mc, int mouseX, int mouseY) {
        RDGuiElement<?> hovered = findTopHovered(mc, mouseX, mouseY);
        if (hovered != null) hovered.onMouseLeave0(mc, mouseX, mouseY);
    }

    @Override
    public void onMousePressed0(Minecraft mc, int mouseX, int mouseY) {
        RDGuiElement<?> hovered = findTopHovered(mc, mouseX, mouseY);
        if (hovered != null) hovered.onMousePressed0(mc, mouseX, mouseY);
    }

    @Override
    public void onMouseReleased0(Minecraft mc, int mouseX, int mouseY) {
        RDGuiElement<?> hovered = findTopHovered(mc, mouseX, mouseY);
        if (hovered != null) hovered.onMouseReleased0(mc, mouseX, mouseY);
    }

    @Override
    public void onMouseDragged0(Minecraft mc, int mouseX, int mouseY, MoveDirection direction, int diffX, int diffY) {
        RDGuiElement<?> hovered = findTopHovered(mc, mouseX, mouseY);
        if (hovered != null) hovered.onMouseDragged0(mc, mouseX, mouseY, direction, diffX, diffY);
    }

    @Override
    public boolean mouseHover(Minecraft mc, int mouseX, int mouseY) {
        return calculatedElementShape.contains(mouseX, mouseY);
    }

    @Override @Nullable
    public RDGuiElement<?> findTopHovered(Minecraft mc, int mouseX, int mouseY) {
        if (!calculatedElementShape.contains(mouseX, mouseY)) return null;
        for (int k = children.size() - 1; k >= 0; k--) {
            RDGuiElement<?> child   = children.get(k);
            RDGuiElement<?> hovered = child.findTopHovered(mc, mouseX, mouseY);
            if (hovered != null) return hovered;
        }
        return this;
    }

    @Override
    public void onDrawBackground(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        drawPanelBackground(mc, mouseX, mouseY, partialTicks);
        for (RDGuiElement<?> child : children) child.onDrawBackground(mc, mouseX, mouseY, partialTicks);
    }

    protected void drawPanelBackground(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        RDGuiTexture texture = texturePack.getCalculatedTexture(getActionState(), getPersistentState());
        if (texture != null) {
            texture.draw(
                    mc,
                    calculatedElementShape.x(), calculatedElementShape.y(),
                    calculatedElementShape.width(), calculatedElementShape.height()
            );
        }
    }

    @Override
    public void onDrawForeground(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        for (RDGuiElement<?> child : children) child.onDrawForeground(mc, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onDrawText(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        for (RDGuiElement<?> child : children) child.onDrawText(mc, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onDrawLast(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        for (RDGuiElement<?> child : children) child.onDrawLast(mc, mouseX, mouseY, partialTicks);
    }

    public void collectElements() {
        for (RDGuiElement<?> child : children) {
            if (child instanceof RDGuiPanel) ((RDGuiPanel<?>) child).collectElements();
            else if (child instanceof GuiButton) screen.getButtonList().add((GuiButton) child);
            else if (child instanceof GuiLabel) screen.getLabelList().add((GuiLabel) child);
            else if (child instanceof RDGuiTextField<?>) screen.getFieldsList().add((RDGuiTextField<?>) child);
        }
    }

    @Override
    public void offsetCalculatedShape(float dx, float dy) {
        calculatedElementShape.offset(dx, dy);
        for (RDGuiElement<?> child : children) {
            child.offsetCalculatedShape(dx, dy);
        }
    }
}