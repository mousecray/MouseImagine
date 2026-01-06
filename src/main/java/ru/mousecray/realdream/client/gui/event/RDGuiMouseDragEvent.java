package ru.mousecray.realdream.client.gui.event;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.realdream.client.gui.RDGuiElement;

@SideOnly(Side.CLIENT)
public class RDGuiMouseDragEvent<T extends RDGuiElement<T>> extends RDGuiMouseMoveEvent<T> {
    private int diffX, diffY;
    private int tickDown;

    void setDiffX(int diffX)       { this.diffX = diffX; }
    void setDiffY(int diffY)       { this.diffY = diffY; }
    void setTickDown(int tickDown) { this.tickDown = tickDown; }

    public int getDiffX()          { return diffX; }
    public int getDiffY()          { return diffY; }
    public int getTickDown()       { return tickDown; }
}