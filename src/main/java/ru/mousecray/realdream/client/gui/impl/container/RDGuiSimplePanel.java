package ru.mousecray.realdream.client.gui.impl.container;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.realdream.client.gui.container.RDGuiPanel;
import ru.mousecray.realdream.client.gui.dim.GuiShape;

@SideOnly(Side.CLIENT)
public class RDGuiSimplePanel extends RDGuiPanel<RDGuiSimplePanel> {
    public RDGuiSimplePanel(GuiShape elementShape) {
        super(elementShape);
    }

    @Override
    public RDGuiSimplePanel self() {
        return this;
    }
}
