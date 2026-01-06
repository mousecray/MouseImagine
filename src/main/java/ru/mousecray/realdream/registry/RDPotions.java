package ru.mousecray.realdream.registry;

import net.minecraft.potion.Potion;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import ru.mousecray.realdream.RealDream;
import ru.mousecray.realdream.Tags;
import ru.mousecray.realdream.common.entity.potion.PotionMagicWallet;
import ru.mousecray.realdream.registry.constants.PotionNames;
import ru.mousecray.realdream.registry.constants.PotionTextures;
import ru.mousecray.realdream.utils.RDReflectionUtils;

@GameRegistry.ObjectHolder(Tags.MOD_ID)
@Mod.EventBusSubscriber
public class RDPotions {
    public static Potion DOUBLE_CRAFT        = RDPlugFactory.CREATE_POTION_PLUG();
    public static Potion DOUBLE_MYTHIC       = RDPlugFactory.CREATE_POTION_PLUG();
    public static Potion DOUBLE_FISHING      = RDPlugFactory.CREATE_POTION_PLUG();
    public static Potion DOUBLE_FARM_HARVEST = RDPlugFactory.CREATE_POTION_PLUG();
    public static Potion IMMORTALITY         = RDPlugFactory.CREATE_POTION_PLUG();

    private static void onInit() {
        DOUBLE_CRAFT = new PotionMagicWallet(PotionNames.DOUBLE_CRAFT_NAME, 0xCFB53B, PotionTextures.DOUBLE_CRAFT);
        DOUBLE_MYTHIC = new PotionMagicWallet(PotionNames.DOUBLE_MYTHIC_NAME, 0xB300B3, PotionTextures.DOUBLE_MYTHIC);
        DOUBLE_FISHING = new PotionMagicWallet(PotionNames.DOUBLE_FISHING_NAME, 0x0000C2, PotionTextures.DOUBLE_FISHING);
        DOUBLE_FARM_HARVEST = new PotionMagicWallet(PotionNames.DOUBLE_FARM_HARVEST_NAME, 0x2BB52B, PotionTextures.DOUBLE_FARM_HARVEST);
        IMMORTALITY = new PotionMagicWallet(PotionNames.IMMORTALITY_NAME, 0x434A52, PotionTextures.IMMORTALITY);
    }

    @SubscribeEvent
    public static void onRegistryPotion(RegistryEvent.Register<Potion> e) {
        onInit();
        RealDream.LOGGER.info("Initialized potions");

        IForgeRegistry<Potion> registry = e.getRegistry();
        RDReflectionUtils.prepare(RDPotions.class).<Potion>getPublicStaticFields().forEach(registry::register);
        RealDream.LOGGER.info("Registered potions");
    }
}