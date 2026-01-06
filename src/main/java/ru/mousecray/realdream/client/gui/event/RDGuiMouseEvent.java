package ru.mousecray.realdream.client.gui.event;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.realdream.client.gui.RDGuiElement;

@SideOnly(Side.CLIENT)
public abstract class RDGuiMouseEvent<T extends RDGuiElement<T>> extends RDGuiEvent<T> { }
