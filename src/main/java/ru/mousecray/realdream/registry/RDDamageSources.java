package ru.mousecray.realdream.registry;

import net.minecraft.util.DamageSource;
import ru.mousecray.realdream.RealDream;
import ru.mousecray.realdream.Tags;
import ru.mousecray.realdream.registry.constants.DamageSourceNames;

public class RDDamageSources {
    public static final RDDamageSources INSTANCE = new RDDamageSources();

    public static DamageSource ON_DROPPED_COIN = RDPlugFactory.CREATE_DAMAGE_SOURCE_PLUG();

    private void onInit() {
        ON_DROPPED_COIN = new DamageSource(Tags.MOD_ID + ":" + DamageSourceNames.ON_DROPPED_COIN_NAME);
    }

    public void register() {
        onInit();
        RealDream.LOGGER.info("Registered DamageSources");
    }
}