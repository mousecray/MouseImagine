package ru.mousecray.realdream.common.economy.capacity;

import ru.mousecray.realdream.common.economy.coin.CoinType;

public interface WalletCapacity<T extends CoinType> {
    long getCapacity(CoinType type);
    Class<T> getSupportedCoins();
    boolean isInfinite(CoinType type);

    default boolean isCoinSupported(CoinType type)                      { return isCoinTypeSupported(type.getClass()); }
    default boolean isCoinTypeSupported(Class<? extends CoinType> type) { return type == getSupportedCoins(); }
}