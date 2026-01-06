package ru.mousecray.realdream.proxy;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import ru.mousecray.realdream.RealDream;
import ru.mousecray.realdream.common.eventhandler.CapabilityHandler;
import ru.mousecray.realdream.common.eventhandler.CoinHandler;
import ru.mousecray.realdream.common.eventhandler.MagicWalletHandler;
import ru.mousecray.realdream.common.eventhandler.PotionEffectHandler;
import ru.mousecray.realdream.registry.*;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        RDCapabilities.INSTANCE.register();
        RDPackets.INSTANCE.register();
        RDTriggers.INSTANCE.register();
        RDDamageSources.INSTANCE.register();
        MinecraftForge.EVENT_BUS.register(new CoinHandler());
        MinecraftForge.EVENT_BUS.register(new MagicWalletHandler());
        MinecraftForge.EVENT_BUS.register(new PotionEffectHandler());
        MinecraftForge.EVENT_BUS.register(new CapabilityHandler());
    }

    public void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(RealDream.INSTANCE, new RDGuiHandler());
    }

    public void postInit(FMLPostInitializationEvent event) {

    }
}