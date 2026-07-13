/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mouseproject.api.client.misc.cache;

import ru.mousecray.mouseproject.api.client.MGuiElement;
import ru.mousecray.mouseproject.api.client.MGuiPanel;
import ru.mousecray.mouseproject.api.client.dim.GuiVector;
import ru.mousecray.mouseproject.api.client.dim.layout.AnchorPos;
import ru.mousecray.mouseproject.api.client.dim.layout.GuiMargin;

public class GuiSimpleCacheBuilder<T extends MGuiElement<?>, D extends MGuiPanel<?>> extends GuiCacheBuilder<T, D, GuiSimpleCacheBuilder<T, D>> {
    @Override
    protected void setObjToParent(D parent, T obj, GuiMargin margin, AnchorPos anchor, GuiVector offset) {
        parent.addChild(obj, margin, anchor, offset);
    }
}