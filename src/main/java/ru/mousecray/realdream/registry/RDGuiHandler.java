package ru.mousecray.realdream.registry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import ru.mousecray.realdream.client.gui.impl.wallet.GuiScreenWallet;
import ru.mousecray.realdream.registry.constants.GuiIdentifiers;

public class RDGuiHandler implements IGuiHandler {
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == GuiIdentifiers.WALLET_OPEN_GUI_ID) return new GuiScreenWallet(player, player.inventory.currentItem);
        return null;
    }
}
