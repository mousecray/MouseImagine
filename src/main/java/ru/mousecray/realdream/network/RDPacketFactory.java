package ru.mousecray.realdream.network;

import net.minecraft.item.ItemStack;
import ru.mousecray.realdream.network.packet.PacketWalletSync;

public class RDPacketFactory {
    public static PacketWalletSync WALLET_SYNC(ItemStack stack) { return new PacketWalletSync(stack); }
}