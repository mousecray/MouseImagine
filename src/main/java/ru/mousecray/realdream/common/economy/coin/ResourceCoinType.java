package ru.mousecray.realdream.common.economy.coin;

import net.minecraft.item.Item;
import ru.mousecray.realdream.registry.RDItems;
import ru.mousecray.realdream.registry.constants.CoinNames;

import java.util.function.Supplier;

public enum ResourceCoinType implements CoinType {
    WOOL(() -> RDItems.WOOL_COIN, CoinNames.WOOL_NAME),
    WOOD(() -> RDItems.WOOD_COIN, CoinNames.WOOD_NAME),
    STONE(() -> RDItems.STONE_COIN, CoinNames.STONE_NAME),
    COAL(() -> RDItems.COAL_COIN, CoinNames.COAL_NAME),
    LAPIS(() -> RDItems.LAPIS_COIN, CoinNames.LAPIS_NAME),
    REDSTONE(() -> RDItems.REDSTONE_COIN, CoinNames.REDSTONE_NAME),
    OBSIDIAN(() -> RDItems.OBSIDIAN_COIN, CoinNames.OBSIDIAN_NAME),
    NETHERITE(() -> RDItems.NETHERITE_COIN, CoinNames.NETHERITE_NAME),
    ;

    private final Supplier<Item> item;
    private final String         name;

    ResourceCoinType(Supplier<Item> item, String name) {
        this.item = item;
        this.name = name;
    }

    @Override public Item getItem() { return item.get(); }

    @Override
    public int getID() {
        return ordinal() + SpecificCoinType.values().length + NormalCoinType.values().length;
    }

    @Override public String getTranslationKey() { return name; }
}