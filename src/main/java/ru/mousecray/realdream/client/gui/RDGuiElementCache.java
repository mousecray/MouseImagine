package ru.mousecray.realdream.client.gui;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class RDGuiElementCache {
    private final Map<String, WeakReference<RDGuiElement<?>>> cache = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T extends RDGuiElement<T>> T get(String key, Class<T> type) {
        WeakReference<RDGuiElement<?>> ref = cache.get(key);
        if (ref != null) {
            RDGuiElement<?> element = ref.get();
            if (type.isInstance(element)) {
                return (T) element;
            } else if (element != null) {
                cache.remove(key);
            }
        }
        return null;
    }

    public <T extends RDGuiElement<T>> void put(String key, T element) {
        cache.put(key, new WeakReference<>(element));
    }

    public void clear() {
        cache.clear();
    }
}
