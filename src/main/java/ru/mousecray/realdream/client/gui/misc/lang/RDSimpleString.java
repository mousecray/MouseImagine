package ru.mousecray.realdream.client.gui.misc.lang;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RDSimpleString implements RDGuiString {
    private final String text;

    public RDSimpleString(String text) {
        this.text = text == null ? "" : text;
    }

    @Override
    public String get() {
        return text;
    }

    @Override
    public String toString() {
        return text;
    }
}
