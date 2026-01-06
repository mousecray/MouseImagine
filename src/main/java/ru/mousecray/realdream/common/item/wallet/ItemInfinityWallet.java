package ru.mousecray.realdream.common.item.wallet;

import mcp.MethodsReturnNonnullByDefault;
import ru.mousecray.realdream.common.economy.capacity.NormalWalletCapacity;
import ru.mousecray.realdream.common.economy.capacity.ResourceWalletCapacity;
import ru.mousecray.realdream.common.economy.capacity.SpecificWalletCapacity;
import ru.mousecray.realdream.common.economy.wallet.WalletType;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemInfinityWallet extends ItemWallet {
    public ItemInfinityWallet(WalletType type) {
        super(type, null, null,
                NormalWalletCapacity.createInfinite(),
                SpecificWalletCapacity.createInfinite(),
                ResourceWalletCapacity.createInfinite());
    }
}