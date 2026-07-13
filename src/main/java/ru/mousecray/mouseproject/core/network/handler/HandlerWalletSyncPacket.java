/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mouseproject.core.network.handler;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import ru.mousecray.mouseproject.core.common.capability.ICapabilityInventory;
import ru.mousecray.mouseproject.core.common.capability.impl.CapabilityWalletInventory;
import ru.mousecray.mouseproject.core.network.packet.PacketWalletSync;

public class HandlerWalletSyncPacket implements IMessageHandler<PacketWalletSync, IMessage> {
    @Override
    public IMessage onMessage(PacketWalletSync message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;
        player.getServerWorld().addScheduledTask(() -> {
            ICapabilityInventory<CapabilityWalletInventory> walletCap = player.getCapability(CapabilityWalletInventory.InventoryProvider.WALLET_INVENTORY, null);
            if (walletCap != null) walletCap.getInventory().setInventorySlotContents(0, message.getStack());
        });
        return null;
    }
}