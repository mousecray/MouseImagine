package ru.mousecray.mouseproject.api.minecraft;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import ru.mousecray.mouseproject.api.container.SoftHashMap;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MouseIBlockState implements Comparable<MouseIBlockState> {
    private static final Map<Block, Map<Integer, MouseIBlockState>> cache = new SoftHashMap<>();
    private final        IBlockState                                state;

    private Boolean isAir = null, isLeaves = null, isFullCube = null, isReplaceable = null, isWood = null, isPassable = null;
    private Boolean isFireSource = null, isBed = null, isLadder = null, isBurning = null, isFlammable = null;
    private Boolean hasTileEntity = null, hasContainer = null, isOpaque = null, isLiquid = null;

    private Float hardness = null, resistance = null;

    public MouseIBlockState(@Nonnull IBlockState state) {
        this.state = Objects.requireNonNull(state);
    }

    public static MouseIBlockState create(IBlockState state) {
        if (RealDream.getInstance().getConfig().ITEM_STATE_CACHE.getBooleanAnyway()) {
            return cache
                    .computeIfAbsent(state.getBlock(), key -> new HashMap<>())
                    .computeIfAbsent(state.getBlock().getMetaFromState(state), key -> new MouseIBlockState(state));
        } else return new MouseIBlockState(state);
    }

    public static MouseIBlockState create(String prefix, String name, int meta) {
        return create(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(prefix, name)).getStateFromMeta(meta));
    }

    public IBlockState getState() { return state; }
    public Material getMaterial() { return state.getMaterial(); }

    public boolean isAir(World world, BlockPos pos) {
        return state.getMaterial() == Material.AIR && state.getBlock().isAir(state, world, pos);
    }

    public boolean isLeaves(World world, BlockPos pos) {
        return state.getMaterial() == Material.LEAVES && state.getBlock().isLeaves(state, world, pos);
    }

    public boolean isFullCube(World world, BlockPos pos, EnumFacing side) {
        return state.getMaterial().isSolid() || state.isNormalCube()
                && state.isNormalCube()
                && state.isSideSolid(world, pos, side);
    }

    public boolean isReplaceable(World world, BlockPos pos) {
        return state.getMaterial().isReplaceable() && state.getBlock().isReplaceable(world, pos);
    }

    public boolean isWood(World world, @Nonnull BlockPos pos) {
        return state.getBlock().isWood(world, pos);
    }

    public boolean isPassable(World world, @Nonnull BlockPos pos) {
        return state.getCollisionBoundingBox(world, pos) == null;
    }

    public boolean isFireSource(World world, @Nonnull BlockPos pos, EnumFacing side) {
        return state.getBlock().isFireSource(world, pos, side);
    }

    public boolean isBed(World world, @Nonnull BlockPos pos) {
        return state.getBlock().isBed(state, world, pos, null);
    }

    public boolean isLadder(World world, @Nonnull BlockPos pos) {
        return state.getBlock().isLadder(state, world, pos, null);
    }

    public boolean isBurning(World world, @Nonnull BlockPos pos) {
        return state.getBlock().isBurning(world, pos);
    }

    public boolean isFlammable(World world, @Nonnull BlockPos pos, EnumFacing side) {
        return state.getBlock().isFlammable(world, pos, side);
    }

    public float getHardness(World world, @Nonnull BlockPos pos) {
        return state.getBlockHardness(world, pos);
    }

    public float getResistance()   { return state.getBlock().getExplosionResistance(null); }
    public boolean hasTileEntity() { return state.getBlock().hasTileEntity(state); }
    public boolean hasContainer()  { return state instanceof BlockContainer; }
    public boolean isOpaque()      { return state.getMaterial().isOpaque() && state.isOpaqueCube(); }
    public boolean isLiquid()      { return state.getMaterial().isLiquid(); }

    public boolean isAirCached(World world, BlockPos pos) {
        return checkCacheDisabled() || isAir == null ? (isAir = isAir(world, pos)) : isAir;
    }

    public boolean isLeavesCached(World world, BlockPos pos) {
        return checkCacheDisabled() || isLeaves == null ? (isLeaves = isLeaves(world, pos)) : isLeaves;
    }

    public boolean isFullCubeCached(World world, BlockPos pos, EnumFacing side) {
        return checkCacheDisabled() || isFullCube == null ? (isFullCube = isFullCube(world, pos, side)) : isFullCube;
    }

    public boolean isReplaceableCached(World world, BlockPos pos) {
        return checkCacheDisabled() || isReplaceable == null ? (isReplaceable = isReplaceable(world, pos)) : isReplaceable;
    }

    public boolean isWoodCached(World world, BlockPos pos) {
        return checkCacheDisabled() || isWood == null ? (isWood = isWood(world, pos)) : isWood;
    }

    public boolean isPassableCached(World world, BlockPos pos) {
        return checkCacheDisabled() || isPassable == null ? (isPassable = isPassable(world, pos)) : isPassable;
    }

    public boolean isFireSourceCached(World world, BlockPos pos, EnumFacing side) {
        return checkCacheDisabled() || isFireSource == null ? (isFireSource = isFireSource(world, pos, side)) : isFireSource;
    }

    public boolean isBedCached(World world, BlockPos pos) {
        return checkCacheDisabled() || isBed == null ? (isBed = isBed(world, pos)) : isBed;
    }

    public boolean isLadderCached(World world, BlockPos pos) {
        return checkCacheDisabled() || isLadder == null ? (isLadder = isLadder(world, pos)) : isLadder;
    }

    public boolean isBurningCached(World world, BlockPos pos) {
        return checkCacheDisabled() || isBurning == null ? (isBurning = isBurning(world, pos)) : isBurning;
    }

    public boolean isFlammableCached(World world, BlockPos pos, EnumFacing side) {
        return checkCacheDisabled() || isFlammable == null ? (isFlammable = isFlammable(world, pos, side)) : isFlammable;
    }

    public boolean hasTileEntityCached() {
        return checkCacheDisabled() || hasTileEntity == null ? (hasTileEntity = hasTileEntity()) : hasTileEntity;
    }

    public boolean hasContainerCached() {
        return checkCacheDisabled() || hasContainer == null ? (hasContainer = hasContainer()) : hasContainer;
    }

    public boolean isOpaqueCached() {
        return checkCacheDisabled() || isOpaque == null ? (isOpaque = isOpaque()) : isOpaque;
    }

    public boolean isLiquidCached() {
        return checkCacheDisabled() || isLiquid == null ? (isLiquid = isLiquid()) : isLiquid;
    }

    public float getHardnessCached(World world, BlockPos pos) {
        return checkCacheDisabled() || hardness == null ? (hardness = getHardness(world, pos)) : hardness;
    }

    public float getResistanceCached() {
        return checkCacheDisabled() || resistance == null ? (resistance = getResistance()) : resistance;
    }

    private static boolean checkCacheDisabled()    { return !RealDream.getInstance().getConfig().BLOCK_STATE_CACHE_DATA.getBooleanAnyway(); }
    public boolean isEqual(MouseIBlockState other) { return other != null && (other == this || state == other.state); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MouseIBlockState)) return false;
        MouseIBlockState that = (MouseIBlockState) o;
        return Objects.equals(getState(), that.getState());
    }

    @Override public int hashCode()    { return Objects.hash(getState()); }
    @Override public String toString() { return state.getBlock().getRegistryName() + ":" + state.getBlock().getMetaFromState(state); }

    @Override
    public int compareTo(MouseIBlockState o) {
        return Comparator
                .<MouseIBlockState, Comparable>comparing((t) -> t.getState().getBlock().getRegistryName())
                .thenComparing((t) -> t.getState().getBlock().getMetaFromState(state))
                .compare(this, o);
    }
}