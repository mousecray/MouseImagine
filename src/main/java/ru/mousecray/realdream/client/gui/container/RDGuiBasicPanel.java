package ru.mousecray.realdream.client.gui.container;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.realdream.client.gui.dim.GuiShape;

@SideOnly(Side.CLIENT)
public class RDGuiBasicPanel extends RDGuiPanel<RDGuiBasicPanel> {
    public RDGuiBasicPanel(GuiShape elementShape) {
        super(elementShape);
    }

    @Override
    public RDGuiBasicPanel self() {
        return this;
    }
}
