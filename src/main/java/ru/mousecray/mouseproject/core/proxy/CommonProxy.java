/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mouseproject.core.proxy;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import ru.mousecray.mouseproject.core.MouseProject;
import ru.mousecray.mouseproject.core.common.eventhandler.CapabilityHandler;
import ru.mousecray.mouseproject.core.common.eventhandler.CoinHandler;
import ru.mousecray.mouseproject.core.common.eventhandler.MagicWalletHandler;
import ru.mousecray.mouseproject.core.common.eventhandler.PotionEffectHandler;
import ru.mousecray.mouseproject.core.registry.*;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        MPCapabilities.INSTANCE.register();
        MPPackets.INSTANCE.register();
        MPTriggers.INSTANCE.register();
        MPDamageSources.INSTANCE.register();
        MinecraftForge.EVENT_BUS.register(new CoinHandler());
        MinecraftForge.EVENT_BUS.register(new MagicWalletHandler());
        MinecraftForge.EVENT_BUS.register(new PotionEffectHandler());
        MinecraftForge.EVENT_BUS.register(new CapabilityHandler());
    }

    public void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(MouseProject.INSTANCE, new MPGuiHandler());
    }

    public void postInit(FMLPostInitializationEvent event) {

    }
}