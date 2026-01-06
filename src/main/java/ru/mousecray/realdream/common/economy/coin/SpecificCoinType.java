package ru.mousecray.realdream.common.economy.coin;

import net.minecraft.item.Item;
import ru.mousecray.realdream.registry.RDItems;
import ru.mousecray.realdream.registry.constants.CoinNames;

import java.util.function.Supplier;

public enum SpecificCoinType implements CoinType {
    MYTHIC(() -> RDItems.MYTHIC_COIN, CoinNames.MYTHIC_NAME),
    ;

    private final Supplier<Item> item;
    private final String         name;

    SpecificCoinType(Supplier<Item> item, String name) {
        this.item = item;
        this.name = name;
    }

    @Override public Item getItem()             { return item.get(); }
    @Override public int getID()                { return ordinal() + NormalCoinType.values().length; }
    @Override public String getTranslationKey() { return name; }
}