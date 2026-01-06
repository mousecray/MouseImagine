package ru.mousecray.realdream.registry;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import ru.mousecray.realdream.Tags;

import javax.annotation.Nonnull;

public class RDCreativeTabs extends CreativeTabs {
    public static final RDCreativeTabs MAIN_TAB = new RDCreativeTabs();
    public RDCreativeTabs()                          { super(Tags.MOD_ID); }
    @Nonnull @Override public ItemStack createIcon() { return new ItemStack(RDItems.RUBY_COIN); }
}
