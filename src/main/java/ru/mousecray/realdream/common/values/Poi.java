package ru.mousecray.realdream.common.values;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import ru.mousecray.realdream.registry.RDVillagerRegistry;

import javax.annotation.Nullable;

public class Poi {
    private final Block    poi;
    private final BlockPos pos;

    public Poi(Block poi, BlockPos pos) {
        this.poi = poi;
        this.pos = pos;
    }

    public Poi(World world, BlockPos pos) {
        poi = world.getBlockState(pos).getBlock();
        this.pos = pos;
    }

    @Nullable
    public VillagerRegistry.VillagerProfession getProfession() {
        return RDVillagerRegistry.getProfessionForBlock(poi);
    }

    public boolean isPOIExist(World world) {
        return world.getBlockState(pos).getBlock() == poi;
    }

    public Block getBlock() {
        return poi;
    }

    public BlockPos getPos() {
        return pos;
    }
}