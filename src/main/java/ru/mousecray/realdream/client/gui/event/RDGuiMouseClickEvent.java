package ru.mousecray.realdream.client.gui.event;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.realdream.client.gui.RDClickType;
import ru.mousecray.realdream.client.gui.RDGuiElement;

@SideOnly(Side.CLIENT)
public class RDGuiMouseClickEvent<T extends RDGuiElement<T>> extends RDGuiMouseEvent<T> {
    private final RDClickType clickType;

    public RDGuiMouseClickEvent(RDClickType clickType) { this.clickType = clickType; }
    public RDClickType getClickType()                  { return clickType; }
}