package ru.mousecray.realdream.common.item.wallet;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import ru.mousecray.realdream.common.economy.CoinValue;
import ru.mousecray.realdream.common.economy.capacity.WalletCapacity;
import ru.mousecray.realdream.common.economy.wallet.WalletType;
import ru.mousecray.realdream.common.item.coin.ICoin;
import ru.mousecray.realdream.nbt.RealDreamNBT;
import ru.mousecray.realdream.utils.RDRandomUtils;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemEpicWallet extends ItemWallet {
    public ItemEpicWallet(WalletType type, WalletCapacity<?>... capacities) {
        super(type, null, null, capacities);
    }

    @Nullable @Override
    public CoinValue putCoin(World world, EntityLivingBase entity, ItemStack wallet, ItemStack coin) {
        CoinValue coinValue = super.putCoin(world, entity, wallet, coin);
        if (coinValue != null && coinValue.isNull()) {
            if (!world.isRemote) {
                if (!(coin.getItem() instanceof ICoin) || wallet.getItem() != this) return null;

                if (RealDreamNBT.get(coin).getCoinPipe().loadIsNew()) {
                    Random rand = entity.getRNG();
                    if (RDRandomUtils.accurateChance(rand, 0.1f)) {
                        super.putCoin(world, entity, wallet, coin.copy());
                    }
                }
            }
        }
        return coinValue;
    }
}