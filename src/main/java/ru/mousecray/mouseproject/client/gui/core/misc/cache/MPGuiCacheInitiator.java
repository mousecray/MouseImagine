/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mouseproject.client.gui.core.misc.cache;

import ru.mousecray.mouseproject.client.gui.core.MPGuiElement;
import ru.mousecray.mouseproject.client.gui.core.MPGuiPanel;
import ru.mousecray.mouseproject.client.gui.core.MPGuiScreen;

import java.lang.ref.WeakReference;
import java.util.function.Supplier;

public class MPGuiCacheInitiator<D extends MPGuiPanel<?>> {
    private final String                     key;
    private       WeakReference<MPGuiScreen> screen;
    private       WeakReference<D>           parent;

    MPGuiCacheInitiator(String key) { this.key = key; }

    public MPGuiCacheInitiator<D> setScreen(MPGuiScreen screen) {
        this.screen = new WeakReference<>(screen);
        return this;
    }

    public MPGuiCacheInitiator<D> setParent(D parent) {
        this.parent = new WeakReference<>(parent);
        return this;
    }

    public <T extends MPGuiElement<?>> MPGuiSimpleCacheBuilder<T, D> construct(Supplier<T> obj) {
        MPGuiSimpleCacheBuilder<T, D> delegate = new MPGuiSimpleCacheBuilder<>();
        delegate.setKey(key);
        delegate.setScreen(screen);
        delegate.setParent(parent);
        delegate.setObj(obj);
        return delegate;
    }

    public <T extends MPGuiElement<?>, M extends MPGuiCacheBuilder<T, D, M>, X extends MPGuiCacheBuilder<T, D, M>> X construct(X delegate, Supplier<T> obj) {
        delegate.setKey(key);
        delegate.setScreen(screen);
        delegate.setParent(parent);
        delegate.setObj(obj);
        return delegate;
    }
}
