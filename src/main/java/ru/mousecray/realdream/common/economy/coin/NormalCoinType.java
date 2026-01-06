package ru.mousecray.realdream.common.economy.coin;

import net.minecraft.item.Item;
import ru.mousecray.realdream.registry.RDItems;
import ru.mousecray.realdream.registry.constants.CoinNames;

import java.util.function.Supplier;

public enum NormalCoinType implements CoinType {
    BRONZE(() -> RDItems.BRONZE_COIN, CoinNames.BRONZE_NAME),
    SILVER(() -> RDItems.SILVER_COIN, CoinNames.SILVER_NAME),
    GOLD(() -> RDItems.GOLD_COIN, CoinNames.GOLD_NAME),
    DIAMOND(() -> RDItems.DIAMOND_COIN, CoinNames.DIAMOND_NAME),
    EMERALD(() -> RDItems.EMERALD_COIN, CoinNames.EMERALD_NAME),
    RUBY(() -> RDItems.RUBY_COIN, CoinNames.RUBY_NAME),
    AMETHYST(() -> RDItems.AMETHYST_COIN, CoinNames.AMETHYST_NAME);

    private final Supplier<Item> item;
    private final String         name;

    NormalCoinType(Supplier<Item> item, String name) {
        this.item = item;
        this.name = name;
    }

    @Override public Item getItem()             { return item.get(); }
    @Override public int getID()                { return ordinal(); }
    @Override public String getTranslationKey() { return name; }
}
