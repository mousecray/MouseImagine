/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mouseproject.client.gui.core.misc.cache;

import ru.mousecray.mouseproject.client.gui.core.MPGuiElement;
import ru.mousecray.mouseproject.client.gui.core.container.MPGuiGridPanel;
import ru.mousecray.mouseproject.client.gui.core.dim.MPAnchorPos;
import ru.mousecray.mouseproject.client.gui.core.dim.MPGuiVector;
import ru.mousecray.mouseproject.client.gui.core.dim.layout.MPGridPos;
import ru.mousecray.mouseproject.client.gui.core.dim.layout.MPGuiMargin;

import java.util.Objects;

public class MPGuiGridCacheBuilder<T extends MPGuiElement<?>, D extends MPGuiGridPanel> extends MPGuiCacheBuilder<T, D, MPGuiGridCacheBuilder<T, D>> {
    private MPGridPos gridPos = MPGridPos.DEFAULT();

    public MPGuiGridCacheBuilder<T, D> setGridPos(MPGridPos gridPos) {
        this.gridPos = Objects.requireNonNull(gridPos);
        return this;
    }

    @Override
    protected void setObjToParent(D parent, T obj, MPGuiMargin margin, MPAnchorPos anchor, MPGuiVector offset) {
        parent.addChild(obj, margin, anchor, offset, gridPos);
    }
}