/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mouseproject.core.network;

import net.minecraft.item.ItemStack;
import ru.mousecray.mouseproject.core.network.packet.PacketWalletSync;

public class MPPacketFactory {
    public static PacketWalletSync WALLET_SYNC(ItemStack stack) { return new PacketWalletSync(stack); }
}