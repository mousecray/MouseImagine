/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mouseproject.client.gui.core.misc.cache;

import ru.mousecray.mouseproject.client.gui.core.MPGuiElement;
import ru.mousecray.mouseproject.client.gui.core.MPGuiPanel;
import ru.mousecray.mouseproject.client.gui.core.dim.MPAnchorPos;
import ru.mousecray.mouseproject.client.gui.core.dim.MPGuiVector;
import ru.mousecray.mouseproject.client.gui.core.dim.layout.MPGuiMargin;

public class MPGuiSimpleCacheBuilder<T extends MPGuiElement<?>, D extends MPGuiPanel<?>> extends MPGuiCacheBuilder<T, D, MPGuiSimpleCacheBuilder<T, D>> {
    @Override
    protected void setObjToParent(D parent, T obj, MPGuiMargin margin, MPAnchorPos anchor, MPGuiVector offset) {
        parent.addChild(obj, margin, anchor, offset);
    }
}