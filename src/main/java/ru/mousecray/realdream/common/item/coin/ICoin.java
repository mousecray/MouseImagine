package ru.mousecray.realdream.common.item.coin;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import ru.mousecray.realdream.common.economy.coin.CoinType;

import java.util.Random;

public interface ICoin {
    void onPickup(World world, Entity entity, ItemStack stack, Random rand);
    void onDropWhenDeath(World world, Entity entity, ItemStack stack, Random rand);
    void onToss(World world, Entity entity, ItemStack stack, Random rand);

    CoinType getCoinType();
    int getCoinID();
}