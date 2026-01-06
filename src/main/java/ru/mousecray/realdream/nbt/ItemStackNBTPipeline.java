package ru.mousecray.realdream.nbt;

public class ItemStackNBTPipeline {
    static final  String                             TAG_BASE_KEY = "Base";
    private final RealDreamNBT.RealDreamNBTItemStack container;
    private ItemStackNBTPipeline(RealDreamNBT.RealDreamNBTItemStack container) { this.container = container; }
    static ItemStackNBTPipeline get(RealDreamNBT.RealDreamNBTItemStack base)   { return new ItemStackNBTPipeline(base); }

    public void saveBase(int value)                                            { container.getBaseTag().setInteger(TAG_BASE_KEY, value); }
}