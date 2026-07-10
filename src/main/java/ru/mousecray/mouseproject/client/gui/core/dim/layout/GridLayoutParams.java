/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mouseproject.client.gui.core.dim.layout;

import ru.mousecray.mouseproject.client.gui.core.dim.MPAnchorPos;
import ru.mousecray.mouseproject.client.gui.core.dim.MPGuiVector;

import javax.annotation.Nullable;

public class GridLayoutParams extends MPGuiLayoutParams {
    public final MPGridPos gridPos;

    public GridLayoutParams(@Nullable MPGuiMargin margin, @Nullable MPAnchorPos anchor, @Nullable MPGuiVector offset, @Nullable MPGridPos gridPos) {
        super(margin, anchor, offset);
        this.gridPos = gridPos != null ? gridPos : MPGridPos.DEFAULT();
    }
}