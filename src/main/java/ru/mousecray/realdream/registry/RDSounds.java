package ru.mousecray.realdream.registry;

import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import ru.mousecray.realdream.RealDream;
import ru.mousecray.realdream.common.sound.RDDefaultSound;
import ru.mousecray.realdream.registry.constants.SoundNames;
import ru.mousecray.realdream.utils.RDReflectionUtils;

@Mod.EventBusSubscriber
public class RDSounds {
    public static SoundEvent WALLET_SHUFFLE_HOTBAR = RDPlugFactory.CREATE_SOUND_PLUG();
    public static SoundEvent WALLET_EFFECT_USE     = RDPlugFactory.CREATE_SOUND_PLUG();

    public static SoundEvent COIN_DROP   = RDPlugFactory.CREATE_SOUND_PLUG();
    public static SoundEvent COIN_PICKUP = RDPlugFactory.CREATE_SOUND_PLUG();

    public static SoundEvent TRADE = RDPlugFactory.CREATE_SOUND_PLUG();

    private static void onInit() {
        WALLET_SHUFFLE_HOTBAR = new RDDefaultSound(SoundNames.WALLET_SHUFFLE_HOTBAR);
        WALLET_EFFECT_USE = new RDDefaultSound(SoundNames.WALLET_EFFECT_USE);

        COIN_DROP = new RDDefaultSound(SoundNames.COIN_DROP);
        COIN_PICKUP = new RDDefaultSound(SoundNames.COIN_PICKUP);

        TRADE = new RDDefaultSound(SoundNames.TRADE);
    }

    @SubscribeEvent
    public static void onRegistrySound(RegistryEvent.Register<SoundEvent> e) {
        onInit();
        RealDream.LOGGER.info("Initialized sounds");

        IForgeRegistry<SoundEvent> registry = e.getRegistry();
        RDReflectionUtils.prepare(RDSounds.class).<SoundEvent>getPublicStaticFields().forEach(registry::register);
        RealDream.LOGGER.info("Registered sounds");
    }
}
