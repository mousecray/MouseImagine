/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mouseproject.api.client.misc.cache;

import ru.mousecray.mouseproject.api.client.MGuiElement;
import ru.mousecray.mouseproject.api.client.MGuiPanel;
import ru.mousecray.mouseproject.api.client.MGuiScreen;

import java.lang.ref.WeakReference;
import java.util.function.Supplier;

public class GuiCacheInitiator<D extends MGuiPanel<?>> {
    private final String                    key;
    private       WeakReference<MGuiScreen> screen;
    private       WeakReference<D>          parent;

    GuiCacheInitiator(String key) { this.key = key; }

    public GuiCacheInitiator<D> setScreen(MGuiScreen screen) {
        this.screen = new WeakReference<>(screen);
        return this;
    }

    public GuiCacheInitiator<D> setParent(D parent) {
        this.parent = new WeakReference<>(parent);
        return this;
    }

    public <T extends MGuiElement<?>> GuiSimpleCacheBuilder<T, D> construct(Supplier<T> obj) {
        GuiSimpleCacheBuilder<T, D> delegate = new GuiSimpleCacheBuilder<>();
        delegate.setKey(key);
        delegate.setScreen(screen);
        delegate.setParent(parent);
        delegate.setObj(obj);
        return delegate;
    }

    public <T extends MGuiElement<?>, M extends GuiCacheBuilder<T, D, M>, X extends GuiCacheBuilder<T, D, M>> X construct(X delegate, Supplier<T> obj) {
        delegate.setKey(key);
        delegate.setScreen(screen);
        delegate.setParent(parent);
        delegate.setObj(obj);
        return delegate;
    }
}
