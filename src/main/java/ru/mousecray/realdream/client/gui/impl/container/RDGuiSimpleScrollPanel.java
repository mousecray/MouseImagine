package ru.mousecray.realdream.client.gui.impl.container;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.realdream.client.gui.container.RDGuiScrollPanel;
import ru.mousecray.realdream.client.gui.dim.GuiShape;

@SideOnly(Side.CLIENT)
public class RDGuiSimpleScrollPanel extends RDGuiScrollPanel<RDGuiSimpleScrollPanel> {
    public RDGuiSimpleScrollPanel(GuiShape elementShape) {
        super(elementShape);
    }

    @Override
    public RDGuiSimpleScrollPanel self() {
        return this;
    }
}
