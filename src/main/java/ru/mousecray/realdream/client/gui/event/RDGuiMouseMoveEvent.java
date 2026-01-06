package ru.mousecray.realdream.client.gui.event;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.realdream.client.gui.MoveDirection;
import ru.mousecray.realdream.client.gui.RDGuiElement;

@SideOnly(Side.CLIENT)
public class RDGuiMouseMoveEvent<T extends RDGuiElement<T>> extends RDGuiMouseEvent<T> {
    private MoveDirection moveDirection;

    void setMoveDirection(MoveDirection moveDirection) { this.moveDirection = moveDirection; }
    public MoveDirection getMoveDirection()            { return moveDirection; }
}