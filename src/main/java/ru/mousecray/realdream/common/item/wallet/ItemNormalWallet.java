package ru.mousecray.realdream.common.item.wallet;

import mcp.MethodsReturnNonnullByDefault;
import ru.mousecray.realdream.common.economy.capacity.WalletCapacity;
import ru.mousecray.realdream.common.economy.wallet.WalletType;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemNormalWallet extends ItemWallet {
    public ItemNormalWallet(WalletType type, WalletType leakType, WalletCapacity<?>... capacities) {
        super(type, leakType, null, capacities);
    }
}