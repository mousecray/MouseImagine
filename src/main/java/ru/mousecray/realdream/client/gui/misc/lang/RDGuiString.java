package ru.mousecray.realdream.client.gui.misc.lang;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface RDGuiString {
    String get();

    static RDGuiString simple(String text) {
        return new RDSimpleString(text);
    }

    static RDGuiString localized(String key, Object... args) {
        return new RDLocalizedString(key, args);
    }
}
