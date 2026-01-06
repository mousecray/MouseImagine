package ru.mousecray.realdream.common.item;

import net.minecraft.item.Item;
import ru.mousecray.realdream.Tags;
import ru.mousecray.realdream.registry.RDCreativeTabs;

public class RDDefaultItem extends Item {
    public RDDefaultItem(String key) {
        setCreativeTab(RDCreativeTabs.MAIN_TAB);
        setRegistryName(key);
        setTranslationKey(Tags.MOD_ID + "." + key);
    }
}
