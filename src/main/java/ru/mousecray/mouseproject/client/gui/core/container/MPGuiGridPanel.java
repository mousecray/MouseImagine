/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mouseproject.client.gui.core.container;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.mouseproject.client.gui.core.MPGuiElement;
import ru.mousecray.mouseproject.client.gui.core.MPGuiPanel;
import ru.mousecray.mouseproject.client.gui.core.dim.*;
import ru.mousecray.mouseproject.client.gui.core.dim.layout.*;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static ru.mousecray.mouseproject.client.gui.core.component.MPGuiRenderHelper.*;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MPGuiGridPanel extends MPGuiPanel<MPGuiGridPanel> {
    private static final MPGridPos GRID_POS_ZERO = new MPGridPos(0, 0);

    private int   gridRows;
    private int   gridCols;
    private float gridGapX = 0f;
    private float gridGapY = 0f;

    public MPGuiGridPanel(MPGuiShape elementShape, int rows, int cols) {
        super(elementShape);
        gridRows = Math.max(1, rows);
        gridCols = Math.max(1, cols);
    }

    public MPGuiGridPanel setGridSize(int rows, int cols) {
        gridRows = Math.max(1, rows);
        gridCols = Math.max(1, cols);
        return this;
    }

    public MPGuiGridPanel setGaps(float gapX, float gapY) {
        gridGapX = gapX;
        gridGapY = gapY;
        return this;
    }

    @Override
    public void measurePreferred(IGuiVector pDefSize, IGuiVector pContentSize, float sugX, float sugY, MPMutableGuiVector result) {
        super.measurePreferred(pDefSize, pContentSize, sugX, sugY, result);
        MPGuiScaleRules rules = getScaleRules();
        if (!rules.isWrapHorizontal() && !rules.isWrapVertical()) return;

        float scaledGapX = calculateFlowComponentX(pDefSize, pContentSize, gridGapX);
        float scaledGapY = calculateFlowComponentY(pDefSize, pContentSize, gridGapY);

        float maxCellW = 0;
        float maxCellH = 0;

        for (MPGuiElement<?> child : children) {
            measureChildWithMargin(pDefSize, pContentSize, child, getChildMargin(child), marginTemp, measureTemp);
            float ml = marginTemp[0], mt = marginTemp[1], mr = marginTemp[2], mb = marginTemp[3];

            MPGuiLayoutParams params = child.getCore().getLayoutParams();
            MPGridPos         pos    = params instanceof GridLayoutParams ? ((GridLayoutParams) params).gridPos : MPGridPos.DEFAULT();

            float childReqW = measureTemp.x() + ml + mr;
            float childReqH = measureTemp.y() + mt + mb;

            float cellW = (childReqW - scaledGapX * (pos.colSpan - 1)) / pos.colSpan;
            float cellH = (childReqH - scaledGapY * (pos.rowSpan - 1)) / pos.rowSpan;

            if (cellW > maxCellW) maxCellW = cellW;
            if (cellH > maxCellH) maxCellH = cellH;
        }

        if (rules.isWrapHorizontal()) {
            float        totalW = maxCellW * gridCols + scaledGapX * Math.max(0, gridCols - 1);
            MPGuiPadding pad    = getPadding();
            totalW += calculateFlowComponentX(pDefSize, pContentSize, pad.getLeft() + pad.getRight());
            result.withX(totalW);
        }
        if (rules.isWrapVertical()) {
            float        totalH = maxCellH * gridRows + scaledGapY * Math.max(0, gridRows - 1);
            MPGuiPadding pad    = getPadding();
            totalH += calculateFlowComponentY(pDefSize, pContentSize, pad.getTop() + pad.getBottom());
            result.withY(totalH);
        }
    }

    public void addChild(MPGuiElement<?> child, @Nullable MPGuiMargin margin, @Nullable MPAnchorPos anchor, @Nullable MPGuiVector offset, @Nullable MPGridPos gridPos) {
        super.addChild(child, margin, anchor, offset);

        child.getCore().setLayoutParams(new GridLayoutParams(margin, anchor, offset, gridPos));
    }

    @Override
    protected void layoutChildren(IGuiVector parentDefaultSize, IGuiVector parentContentSize, MPMutableGuiShape inner) {
        if (gridRows <= 0 || gridCols <= 0) return;

        float scaledGapX = calculateFlowComponentX(parentDefaultSize, parentContentSize, gridGapX);
        float scaledGapY = calculateFlowComponentY(parentDefaultSize, parentContentSize, gridGapY);

        float availW = inner.width() - scaledGapX * (gridCols - 1);
        float availH = inner.height() - scaledGapY * (gridRows - 1);

        float cellW = Math.max(0, availW / gridCols);
        float cellH = Math.max(0, availH / gridRows);

        for (MPGuiElement<?> child : children) {
            MPGuiLayoutParams params = child.getCore().getLayoutParams();

            MPAnchorPos anchor = params.anchor;

            MPGridPos pos = params instanceof GridLayoutParams ? ((GridLayoutParams) params).gridPos : MPGridPos.DEFAULT();

            float cellAreaX = inner.x() + pos.col * (cellW + scaledGapX);
            float cellAreaY = inner.y() + pos.row * (cellH + scaledGapY);
            float cellAreaW = cellW * pos.colSpan + scaledGapX * (pos.colSpan - 1);
            float cellAreaH = cellH * pos.rowSpan + scaledGapY * (pos.rowSpan - 1);

            measureChildWithMargin(parentDefaultSize, parentContentSize, child, getChildMargin(child), marginTemp, measureTemp);
            float ml = marginTemp[0], mt = marginTemp[1], mr = marginTemp[2], mb = marginTemp[3];

            float childAvailW = Math.max(0, cellAreaW - ml - mr);
            float childAvailH = Math.max(0, cellAreaH - mt - mb);

            child.measurePreferred(parentDefaultSize, parentContentSize, childAvailW, childAvailH, measureTemp);
            float childW = measureTemp.x();
            float childH = measureTemp.y();

            float childX = cellAreaX + ml;
            float childY = cellAreaY + mt;

            MPGuiVector offset  = getChildOffset(child);
            float       offsetX = calculateFlowComponentX(parentDefaultSize, parentContentSize, offset.x());
            float       offsetY = calculateFlowComponentY(parentDefaultSize, parentContentSize, offset.y());

            switch (anchor) {
                case TOP_LEFT:
                    childX += offsetX;
                    childY += offsetY;
                    break;
                case TOP_CENTER:
                    childX += (childAvailW - childW) / 2 + offsetX;
                    childY += offsetY;
                    break;
                case TOP_RIGHT:
                    childX += childAvailW - childW + offsetX;
                    childY += offsetY;
                    break;
                case MIDDLE_LEFT:
                    childX += offsetX;
                    childY += (childAvailH - childH) / 2 + offsetY;
                    break;
                case MIDDLE_CENTER:
                    childX += (childAvailW - childW) / 2 + offsetX;
                    childY += (childAvailH - childH) / 2 + offsetY;
                    break;
                case MIDDLE_RIGHT:
                    childX += childAvailW - childW + offsetX;
                    childY += (childAvailH - childH) / 2 + offsetY;
                    break;
                case BOTTOM_LEFT:
                    childX += offsetX;
                    childY += childAvailH - childH + offsetY;
                    break;
                case BOTTOM_CENTER:
                    childX += (childAvailW - childW) / 2 + offsetX;
                    childY += childAvailH - childH + offsetY;
                    break;
                case BOTTOM_RIGHT:
                    childX += childAvailW - childW + offsetX;
                    childY += childAvailH - childH + offsetY;
                    break;
            }

            childAvailableTemp.withX(childX).withY(childY).withWidth(childW).withHeight(childH);
            child.calculate(parentDefaultSize, parentContentSize, childAvailableTemp);
        }
    }
}