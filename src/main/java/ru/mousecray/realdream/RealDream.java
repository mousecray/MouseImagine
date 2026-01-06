package ru.mousecray.realdream;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.mousecray.realdream.proxy.ClientProxy;
import ru.mousecray.realdream.proxy.CommonProxy;
import ru.mousecray.realdream.proxy.ServerProxy;


@Mod(modid = Tags.MOD_ID, name = Tags.MOD_ID, version = Tags.VERSION)
public class RealDream {
    public static RealDream INSTANCE;
    public static Logger    LOGGER;

    private final CommonProxy proxy;

    public RealDream() {
        proxy = FMLCommonHandler.instance().getSide().isClient() ? new ClientProxy() : new ServerProxy();
        INSTANCE = this;
        LOGGER = LogManager.getLogger(Tags.MOD_ID);
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
//        RDVillagerRegistry.clearVanilla();
//        RDVillagerRegistry.register();
        proxy.postInit(event);
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
    }

    public CommonProxy getProxy() { return proxy; }
}