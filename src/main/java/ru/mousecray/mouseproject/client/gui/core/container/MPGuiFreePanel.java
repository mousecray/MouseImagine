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
import ru.mousecray.mouseproject.client.gui.core.dim.IGuiVector;
import ru.mousecray.mouseproject.client.gui.core.dim.MPGuiShape;
import ru.mousecray.mouseproject.client.gui.core.dim.MPMutableGuiShape;

import javax.annotation.ParametersAreNonnullByDefault;

import static ru.mousecray.mouseproject.client.gui.core.component.MPGuiRenderHelper.*;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MPGuiFreePanel extends MPGuiPanel<MPGuiFreePanel> {
    public MPGuiFreePanel(MPGuiShape elementShape) { super(elementShape); }

    @Override
    protected void layoutChildren(IGuiVector parentDefaultSize, IGuiVector parentContentSize, MPMutableGuiShape inner) {
        for (MPGuiElement<?> child : children) {
            if (!child.isVisible()) continue;

            measureChildWithMargin(parentDefaultSize, parentContentSize, child,
                    getChildMargin(child), marginTemp, measureTemp
            );
            float ml     = marginTemp[0], mt = marginTemp[1];
            float childW = measureTemp.x();
            float childH = measureTemp.y();

            float posX = child.getScaleRules().isFixedHorizontal() ? child.getShape().x()
                    : calculateFlowComponentX(parentDefaultSize, parentContentSize, child.getShape().x());
            float posY = child.getScaleRules().isFixedVertical() ? child.getShape().y()
                    : calculateFlowComponentY(parentDefaultSize, parentContentSize, child.getShape().y());

            float[] scaledOffset = calculateScaledOffset(child, parentDefaultSize, parentContentSize);
            float   offsetX      = scaledOffset[0], offsetY = scaledOffset[1];

            childAvailableTemp.withX(inner.x() + ml + posX + offsetX);
            childAvailableTemp.withY(inner.y() + mt + posY + offsetY);
            childAvailableTemp.withWidth(childW);
            childAvailableTemp.withHeight(childH);

            child.calculate(parentDefaultSize, parentContentSize, childAvailableTemp);
        }
    }
}