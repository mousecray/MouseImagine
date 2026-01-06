package ru.mousecray.realdream.registry;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import ru.mousecray.realdream.Tags;
import ru.mousecray.realdream.network.handler.HandlerWalletSyncPacket;
import ru.mousecray.realdream.network.packet.PacketWalletSync;

public class RDPackets {
    public static final RDPackets            INSTANCE = new RDPackets();
    private             int                  lastID;
    private final       SimpleNetworkWrapper NETWORK  = NetworkRegistry.INSTANCE.newSimpleChannel(Tags.MOD_ID);

    private RDPackets() { }

    public void register() {
        registerPacket(HandlerWalletSyncPacket.class, PacketWalletSync.class, Side.SERVER);
    }

    private <REQ extends IMessage, REPLY extends IMessage> void registerPacket(Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, Side side) {
        NETWORK.registerMessage(messageHandler, requestMessageType, lastID++, side);
    }

    public void sendToServer(IMessage message)                  { NETWORK.sendToServer(message); }
    public void sendTo(IMessage message, EntityPlayerMP player) { NETWORK.sendTo(message, player); }
}