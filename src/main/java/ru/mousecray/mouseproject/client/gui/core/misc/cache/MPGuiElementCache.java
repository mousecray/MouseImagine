/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mouseproject.client.gui.core.misc.cache;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.mouseproject.client.gui.core.MPGuiElement;
import ru.mousecray.mouseproject.client.gui.core.MPGuiScreen;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SideOnly(Side.CLIENT)
public class MPGuiElementCache {
    public static final MPGuiElementCache INSTANCE = new MPGuiElementCache();

    private final Map<String, SoftReference<MPGuiElement<?>>> cache = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T extends MPGuiElement<?>> T get(MPGuiScreen screen, String key) {
        SoftReference<MPGuiElement<?>> ref = cache.get(screen.getScreenName() + ":" + key);
        return ref != null ? (T) ref.get() : null;
    }

    public <T extends MPGuiElement<?>> T getOrCreateCEF(MPGuiScreen screen, String key, Supplier<T> typeSupplier, Consumer<T> createAction, Consumer<T> existAction, Consumer<T> finalAction) {
        T t = get(screen, key);
        if (t == null) {
            put(screen, key, t = typeSupplier.get());
            if (createAction != null) createAction.accept(t);
        } else if (existAction != null) existAction.accept(t);
        if (finalAction != null) finalAction.accept(t);
        return t;
    }

    public <T extends MPGuiElement<?>> T getOrCreateCF(MPGuiScreen screen, String key, Supplier<T> typeSupplier, Consumer<T> createAction, Consumer<T> finalAction) {
        return getOrCreateCEF(screen, key, typeSupplier, createAction, null, finalAction);
    }

    public <T extends MPGuiElement<?>> T getOrCreateEF(MPGuiScreen screen, String key, Supplier<T> typeSupplier, Consumer<T> existAction, Consumer<T> finalAction) {
        return getOrCreateCEF(screen, key, typeSupplier, null, existAction, finalAction);
    }

    public <T extends MPGuiElement<?>> T getOrCreateCE(MPGuiScreen screen, String key, Supplier<T> typeSupplier, Consumer<T> createAction, Consumer<T> existAction) {
        return getOrCreateCEF(screen, key, typeSupplier, createAction, existAction, null);
    }

    public <T extends MPGuiElement<?>> T getOrCreateC(MPGuiScreen screen, String key, Supplier<T> typeSupplier, Consumer<T> createAction) {
        return getOrCreateCEF(screen, key, typeSupplier, createAction, null, null);
    }

    public <T extends MPGuiElement<?>> T getOrCreateE(MPGuiScreen screen, String key, Supplier<T> typeSupplier, Consumer<T> existAction) {
        return getOrCreateCEF(screen, key, typeSupplier, null, existAction, null);
    }

    public <T extends MPGuiElement<?>> T getOrCreateF(MPGuiScreen screen, String key, Supplier<T> typeSupplier, Consumer<T> finalAction) {
        return getOrCreateCEF(screen, key, typeSupplier, null, null, finalAction);
    }

    public <T extends MPGuiElement<?>> T getOrCreate(MPGuiScreen screen, String key, Supplier<T> typeSupplier) {
        return getOrCreateCEF(screen, key, typeSupplier, null, null, null);
    }

    public <T extends MPGuiElement<?>> void put(MPGuiScreen screen, String key, T element) {
        cache.put(screen.getScreenName() + ":" + key, new SoftReference<>(element));
    }

    public void clear() {
        cache.clear();
    }
}
