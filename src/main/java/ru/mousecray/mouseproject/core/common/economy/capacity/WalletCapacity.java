/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mouseproject.core.common.economy.capacity;

import ru.mousecray.mouseproject.core.common.economy.coin.CoinType;

public interface WalletCapacity<T extends CoinType> {
    long getCapacity(CoinType type);
    Class<T> getSupportedCoins();
    boolean isInfinite(CoinType type);

    default boolean isCoinSupported(CoinType type)                      { return isCoinTypeSupported(type.getClass()); }
    default boolean isCoinTypeSupported(Class<? extends CoinType> type) { return type == getSupportedCoins(); }
}