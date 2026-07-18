/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mouseproject.api.minecraft;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import ru.mousecray.mouseproject.api.container.SoftHashMap;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MouseIItemState implements Comparable<MouseIItemState> {
    private static final   Map<Item, Map<Integer, MouseIItemState>> cache = new SoftHashMap<>();
    @Nonnull private final Item                                     item;
    private final          int                                      meta;

    private Boolean hasBlock = null;

    private Integer maxStackSize = null;

    private IBlockState block = null;

    public MouseIItemState(@Nonnull Item item, int meta) {
        this.item = Objects.requireNonNull(item);
        this.meta = Math.max(meta, 0);
    }

    public MouseIItemState(Item item) { this(item, 0); }

    public static MouseIItemState create(Item item) {
        return create(item, 0);
    }

    public static MouseIItemState create(Item item, int meta) {
        if (RealDream.getInstance().getConfig().ITEM_STATE_CACHE.getBooleanAnyway()) {
            return cache
                    .computeIfAbsent(item, key -> new HashMap<>())
                    .computeIfAbsent(meta, key -> new MouseIItemState(item, meta));
        } else return new MouseIItemState(item, meta);
    }

    public static MouseIItemState create(String prefix, String name, int meta) {
        return create(ForgeRegistries.ITEMS.getValue(new ResourceLocation(prefix, name)), meta);
    }

    public boolean hasBlock() {
        return item instanceof ItemBlock;
    }

    public int getMaxStackSize(ItemStack stack) {
        return item.getItemStackLimit(stack);
    }
    public int getMaxStackSize() { return getMaxStackSize(null); }

    public IBlockState getBlock() {
        Block b = Block.getBlockFromItem(item);
        return b != net.minecraft.init.Blocks.AIR ? b.getStateFromMeta(meta) : null
    }

    public boolean hasBlockCached() {
        return checkCacheDisabled() || hasBlock == null ? (hasBlock = hasBlock()) : hasBlock;
    }

    public int getMaxStackSizeCached(ItemStack stack) {
        return checkCacheDisabled() || maxStackSize == null ? (maxStackSize = getMaxStackSize(stack)) : maxStackSize;
    }

    public int getMaxStackSizeCached() { return getMaxStackSizeCached(null); }

    public IBlockState getBlockCached() {
        return checkCacheDisabled() || block == null ? (block = getBlock()) : block;
    }

    @Nonnull public Item getItem() { return item; }
    public int getMeta()           { return meta; }

    public ItemStack getItemStack(int stackSize) {
        return new ItemStack(item, Math.min(stackSize, getMaxStackSize(null)), meta);
    }

    public ItemStack getItemStackCached(int stackSize) {
        return new ItemStack(item, Math.min(stackSize, getMaxStackSizeCached(null)), meta);
    }

    public ItemStack getItemStack()               { return new ItemStack(item, 1, meta); }

    public boolean isEqual(MouseIItemState other) { return other != null && (other == this || item == other.item && meta == other.meta); }
    private static boolean checkCacheDisabled()   { return !RealDream.getInstance().getConfig().ITEM_STATE_CACHE_DATA.getBooleanAnyway(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MouseIItemState)) return false;
        MouseIItemState that = (MouseIItemState) o;
        return meta == that.meta && Objects.equals(item, that.item);
    }

    @Override public int hashCode()    { return Objects.hash(item, meta); }
    @Override public String toString() { return item.getRegistryName() + ":" + meta; }

    @Override
    public int compareTo(MouseIItemState o) {
        return Comparator
                .<MouseIItemState, Comparable>comparing((t) -> t.getItem().getRegistryName())
                .thenComparing(MouseIItemState::getMeta)
                .compare(this, o);
    }
}