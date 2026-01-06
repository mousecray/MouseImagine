package ru.mousecray.realdream.common.entity.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import ru.mousecray.realdream.nbt.EntityNBTPipeline;
import ru.mousecray.realdream.nbt.RealDreamNBT;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class PotionPass extends RDDefaultPotion {

    public PotionPass(String name) { super(name, false, 0x000000); }

    @Override
    protected void onAddEffectToEntity(@Nonnull World world, @Nonnull EntityLivingBase entity, PotionEffect effect) {
        EntityNBTPipeline pipe = RealDreamNBT.get(entity).getDefaultPipe();
        pipe.removePotionFirstTick(this);
        entity.removePotionEffect(this);
    }

    @Override public boolean isReady(int duration, int amplifier)     { return false; }
    @Override public boolean isInstant()                              { return true; }
    @Override public boolean shouldRender(PotionEffect effect)        { return false; }
    @Override public boolean shouldRenderInvText(PotionEffect effect) { return false; }
    @Override public boolean shouldRenderHUD(PotionEffect effect)     { return false; }
}