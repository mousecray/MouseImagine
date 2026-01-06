package ru.mousecray.realdream.client.gui.event;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.realdream.client.gui.RDGuiElement;

@SideOnly(Side.CLIENT)
public class RDGuiTickEvent<T extends RDGuiElement<T>> extends RDGuiEvent<T> {
    private float partialTick;

    void setPartialTick(float partialTick) { this.partialTick = partialTick; }
    public float getPartialTick()          { return partialTick; }
}