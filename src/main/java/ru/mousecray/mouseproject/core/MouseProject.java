/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mouseproject.core;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import ru.mousecray.mouseproject.Tags;
import ru.mousecray.mouseproject.api.log.ConsoleColor;
import ru.mousecray.mouseproject.api.log.MouseLogger;
import ru.mousecray.mouseproject.core.proxy.ClientProxy;
import ru.mousecray.mouseproject.core.proxy.CommonProxy;
import ru.mousecray.mouseproject.core.proxy.ServerProxy;


@Mod(modid = Tags.MOD_ID, name = Tags.MOD_ID, version = Tags.VERSION)
public class MouseProject {
    private static MouseProject       instance;
    private final  MouseProjectConfig config;
    private final  MouseLogger        logger;

    private final CommonProxy proxy;

    public MouseProject() {
        proxy = FMLCommonHandler.instance().getSide().isClient() ? new ClientProxy() : new ServerProxy();
        instance = this;
        logger = new MouseLogger(LogManager.getLogger(Tags.MOD_ID), "[${value}]", ConsoleColor.PURPLE);
        config = new MouseProjectConfig(Tags.MOD_NAME, logger);
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
//        MPVillagerRegistry.clearVanilla();
//        MPVillagerRegistry.registerBinary();
        proxy.postInit(event);
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
    }

    public CommonProxy getProxy()            { return proxy; }
    public MouseProjectConfig getConfig()    { return config; }
    public static MouseProject getInstance() { return instance; }
    public MouseLogger getLogger()           { return logger; }
}