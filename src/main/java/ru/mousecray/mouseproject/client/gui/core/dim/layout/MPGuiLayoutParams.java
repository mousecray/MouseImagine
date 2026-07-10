/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mouseproject.client.gui.core.dim.layout;

import ru.mousecray.mouseproject.client.gui.core.dim.MPAnchorPos;
import ru.mousecray.mouseproject.client.gui.core.dim.MPGuiVector;

import javax.annotation.Nullable;

public class MPGuiLayoutParams {
    public final MPGuiMargin margin;
    public final MPAnchorPos anchor;
    public final MPGuiVector offset;

    public MPGuiLayoutParams(@Nullable MPGuiMargin margin, @Nullable MPAnchorPos anchor, @Nullable MPGuiVector offset) {
        this.margin = margin != null ? margin : MPGuiMargin.ZERO();
        this.anchor = anchor != null ? anchor : MPAnchorPos.TOP_LEFT;
        this.offset = offset != null ? offset : MPGuiVector.ZERO;
    }
}