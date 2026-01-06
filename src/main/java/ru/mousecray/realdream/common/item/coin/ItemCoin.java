package ru.mousecray.realdream.common.item.coin;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import ru.mousecray.realdream.common.economy.CoinValue;
import ru.mousecray.realdream.common.economy.coin.CoinType;
import ru.mousecray.realdream.common.item.RDDefaultItem;
import ru.mousecray.realdream.nbt.RealDreamNBT;
import ru.mousecray.realdream.registry.RDSounds;
import ru.mousecray.realdream.registry.RDTriggers;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ItemCoin extends RDDefaultItem implements ICoin {
    private final CoinType type;

    public ItemCoin(CoinType type) {
        super(type.getTranslationKey());
        this.type = type;
    }

    @Override public CoinType getCoinType() { return type; }
    @Override public int getCoinID()        { return type.getID(); }

    @Override
    public void onPickup(World world, Entity entity, ItemStack stack, Random rand) {
        Item item = stack.getItem();
        if (item != this) return;

        RealDreamNBT.RealDreamNBTItemStack stackNBT = RealDreamNBT.get(stack);
        stackNBT.getCoinPipe().removeIsNew();
        stackNBT.removeAllTagIfEmpty();

        world.playSound(null, entity.getPosition(), RDSounds.COIN_PICKUP, SoundCategory.PLAYERS, 1F, 1f + (rand.nextFloat() - 0.5f));

        if (world.isRemote) return;
        if (entity instanceof EntityPlayer) {
            RDTriggers.GET_COIN.trigger(
                    ((EntityPlayerMP) entity),
                    CoinValue.create(stack.getCount(), ((ICoin) item).getCoinType())
            );
        }
    }

    @Override
    public void onToss(World world, Entity entity, ItemStack stack, Random rand) {
        world.playSound(null, entity.getPosition(), RDSounds.COIN_DROP, SoundCategory.PLAYERS, 1F, 1f + (rand.nextFloat() - 0.5f));
    }

    @Override
    public void onDropWhenDeath(World world, Entity entity, ItemStack stack, Random rand) {
        RealDreamNBT.get(stack).getCoinPipe().setIsNew();
    }
}