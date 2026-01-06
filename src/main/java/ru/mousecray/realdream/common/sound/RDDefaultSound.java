package ru.mousecray.realdream.common.sound;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import ru.mousecray.realdream.Tags;

public class RDDefaultSound extends SoundEvent {
    public RDDefaultSound(String key) {
        super(new ResourceLocation(
                Tags.MOD_ID,
                key));
        setRegistryName(key);
    }
}
