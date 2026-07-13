/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mouseproject.core.common.item.wallet;

import mcp.MethodsReturnNonnullByDefault;
import ru.mousecray.mouseproject.core.common.economy.capacity.NormalWalletCapacity;
import ru.mousecray.mouseproject.core.common.economy.capacity.ResourceWalletCapacity;
import ru.mousecray.mouseproject.core.common.economy.capacity.SpecificWalletCapacity;
import ru.mousecray.mouseproject.core.common.economy.wallet.WalletType;

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