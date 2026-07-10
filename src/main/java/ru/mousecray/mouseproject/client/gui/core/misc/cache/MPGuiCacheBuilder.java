/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mouseproject.client.gui.core.misc.cache;

import ru.mousecray.mouseproject.MouseProject;
import ru.mousecray.mouseproject.client.gui.core.MPGuiElement;
import ru.mousecray.mouseproject.client.gui.core.MPGuiPanel;
import ru.mousecray.mouseproject.client.gui.core.MPGuiScreen;
import ru.mousecray.mouseproject.client.gui.core.dim.MPAnchorPos;
import ru.mousecray.mouseproject.client.gui.core.dim.MPGuiVector;
import ru.mousecray.mouseproject.client.gui.core.dim.layout.MPGuiMargin;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class MPGuiCacheBuilder<T extends MPGuiElement<?>, D extends MPGuiPanel<?>, M extends MPGuiCacheBuilder<T, D, M>> {
    private WeakReference<MPGuiScreen> screen;
    private WeakReference<D>           parent;
    private String                     key;
    private Supplier<T>                obj;

    private Consumer<T> createAction, existAction, finalAction;
    private MPGuiMargin margin = MPGuiMargin.ZERO();
    private MPAnchorPos anchor = MPAnchorPos.TOP_LEFT;
    private MPGuiVector offset = MPGuiVector.ZERO;

    @SuppressWarnings("unchecked") protected M self()                                 { return (M) this; }

    public static <D extends MPGuiPanel<?>> MPGuiCacheInitiator<D> create(String key) { return new MPGuiCacheInitiator<>(key); }

    public M setCreateAction(Consumer<T> createAction) {
        this.createAction = createAction;
        return self();
    }

    public M setExistAction(Consumer<T> existAction) {
        this.existAction = existAction;
        return self();
    }

    public M setFinalAction(Consumer<T> finalAction) {
        this.finalAction = finalAction;
        return self();
    }

    public M setMargin(MPGuiMargin margin) {
        this.margin = margin;
        return self();
    }

    public M setAnchor(MPAnchorPos anchor) {
        this.anchor = anchor;
        return self();
    }

    public M setOffset(MPGuiVector offset) {
        this.offset = offset;
        return self();
    }

    void setObj(Supplier<T> obj)                      { this.obj = Objects.requireNonNull(obj); }
    void setParent(WeakReference<D> parent)           { this.parent = parent; }
    void setScreen(WeakReference<MPGuiScreen> screen) { this.screen = Objects.requireNonNull(screen); }
    void setKey(String key)                           { this.key = Objects.requireNonNull(key); }

    protected abstract void setObjToParent(D parent, T obj, MPGuiMargin margin, MPAnchorPos anchor, MPGuiVector offset);

    public M setMeasure(MPGuiMargin margin, MPAnchorPos anchor, MPGuiVector offset) {
        this.margin = margin;
        this.anchor = anchor;
        this.offset = offset;
        return self();
    }

    public T build() {
        MPGuiScreen scr = screen.get();
        if (scr != null) {
            T element = MPGuiElementCache.INSTANCE.getOrCreateCEF(
                    scr, key, obj, createAction, existAction, finalAction
            );

            element.setScreen(scr);

            if (parent != null) {
                D par = parent.get();
                if (par != null) setObjToParent(par, element, margin, anchor, offset);
                else scr.addChild(element, margin, anchor, offset);
            } else scr.addChild(element, margin, anchor, offset);
            return element;
        } else {
            MouseProject.LOGGER.warn("Attempt to create cache element for non-existent MPGuiScreen");
        }
        throw new IllegalStateException("Error when creating cache element for non-existent MPGuiScreen");
    }
}