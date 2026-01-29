package ru.mousecray.realdream.client.gui.container;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.realdream.client.gui.dim.GuiShape;

@SideOnly(Side.CLIENT)
public class RDGuiBasicScrollPanel extends RDGuiScrollPanel<RDGuiBasicScrollPanel> {
    public RDGuiBasicScrollPanel(GuiShape elementShape) {
        super(elementShape);
    }

    @Override
    public RDGuiBasicScrollPanel self() {
        return this;
    }
}
