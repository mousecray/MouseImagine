package ru.mousecray.mouseproject.api.customtype.values;

import net.minecraft.item.Item;
import ru.mousecray.mouseproject.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mouseproject.api.customtype.CustomType;
import ru.mousecray.mouseproject.api.error.UnsupportedValException;
import ru.mousecray.mouseproject.api.error.ValueFormatException;
import ru.mousecray.mouseproject.api.minecraft.IItemState;
import ru.mousecray.mouseproject.api.utils.MouseNumbers;
import ru.mousecray.mouseproject.api.utils.MouseStrings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Comparator;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public class MinecraftItem implements CustomType<MinecraftItem> {
    static { storage.put(MinecraftItem.class, MinecraftItem::parse); }

    @Nonnull protected final String prefix;
    @Nonnull protected final String name;
    protected final          int    meta;

    protected MinecraftItem(String prefix, String name, int meta) {
        this.prefix = prefix;
        this.name = name;
        this.meta = meta;
    }

    @SuppressWarnings("DataFlowIssue")
    public static MinecraftItem create(String prefix, String name, int meta) {
        prefix = MouseStrings.trimWith(Objects.requireNonNull(prefix), true, '\t');
        if (prefix.isEmpty()) throw new ValueFormatException("prefix cannot be empty");
        if (prefix.contains("\t")) throw new ValueFormatException("prefix cannot contains tabs");
        if (prefix.contains(" ")) throw new ValueFormatException("prefix cannot contains space");
        name = MouseStrings.trimWith(Objects.requireNonNull(name), true, '\t');
        if (name.isEmpty()) throw new ValueFormatException("name cannot be empty");
        if (name.contains("\t")) throw new ValueFormatException("name cannot contains tabs");
        if (name.contains(" ")) throw new ValueFormatException("name cannot contains space");
        meta = Math.max(meta, 0);
        return new MinecraftItem(prefix, name, meta);
    }

    public static MinecraftItem create(String name, int meta) {
        return create("minecraft", name, meta);
    }

    @Override public boolean isLess(MinecraftItem other)        { return false; }
    @Override public boolean isMore(MinecraftItem other)        { return false; }
    @Override public boolean isLessOrEqual(MinecraftItem other) { return isEqual(other); }
    @Override public boolean isMoreOrEqual(MinecraftItem other) { return isEqual(other); }

    @Override
    public boolean isEqual(MinecraftItem other) {
        return prefix.equals(other.prefix) && name.equals(other.name) && meta == other.meta;
    }

    @Override public boolean isLessValue(Object other)        { return false; }
    @Override public boolean isMoreValue(Object other)        { return false; }
    @Override public boolean isMoreOrEqualValue(Object other) { return isEqualValue(other); }
    @Override public boolean isLessOrEqualValue(Object other) { return isEqualValue(other); }

    @Override
    public boolean isEqualValue(Object other) {
        return other instanceof MinecraftItem && isEqual(((MinecraftItem) other));
    }

    @Override public MinecraftItem plus(MinecraftItem other)     { return this; }
    @Override public MinecraftItem minus(MinecraftItem other)    { return this; }
    @Override public MinecraftItem divide(MinecraftItem other)   { return this; }
    @Override public MinecraftItem multiply(MinecraftItem other) { return this; }
    @Override public MinecraftItem modulo(MinecraftItem other)   { return this; }
    @Override public MinecraftItem invert()                      { return this; }

    @Override public MinecraftItem plusValue(Object other)       { return this; }
    @Override public MinecraftItem minusValue(Object other)      { return this; }
    @Override public MinecraftItem divideValue(Object other)     { return this; }
    @Override public MinecraftItem multiplyValue(Object other)   { return this; }
    @Override public MinecraftItem moduloValue(Object other)     { return this; }

    @Override
    public MinecraftItem fromValue(Object other) {
        return other instanceof String ? fromString(((String) other)) : CustomType.super.fromValue(other);
    }

    @Override
    public MinecraftItem fromString(String other) throws ValueFormatException {
        MinecraftItem obj = parse(prefix, other);
        if (obj == null) throw new ValueFormatException("String broken");
        return obj;
    }

    @Override public String asString() { return prefix + ":" + name + ":" + meta; }

    @SuppressWarnings("unchecked") @Override
    public <TYPE> TYPE asValue(Class<TYPE> clazz) {
        return clazz == String.class ? (TYPE) asString() : CustomType.super.asValue(clazz);
    }

    @Override public Class<MinecraftItem> getTypeClass() { return MinecraftItem.class; }

    public String getPrefix()                            { return prefix; }
    public String getName()                              { return name; }
    public int getMeta()                                 { return meta; }

    @Nullable
    public IItemState getItemState() {
        Item item = (Item) Item.itemRegistry.getObject(prefix + ':' + name);
        return item != null ? IItemState.create(item, meta) : null;
    }

    public static MinecraftItem parse(@Nullable Object value) throws UnsupportedValException, ValueFormatException {
        if (value instanceof MinecraftItem) return ((MinecraftItem) value);
        else if (value instanceof String) return parse(null, ((String) value));
        throw new UnsupportedValException();
    }

    @SuppressWarnings({ "DataFlowIssue" })
    public static MinecraftItem parse(@Nullable String expectedPrefix, @Nullable String value) throws ValueFormatException {
        if (value == null) throw new ValueFormatException();

        String[] split = value.split(":");
        String   name  = null;
        int      meta  = 0;

        if (split.length > 0) {
            String prefixCandidate = MouseStrings.trimWith(split[0], true, '\t');
            if (!prefixCandidate.isEmpty() && !prefixCandidate.contains(" ") && !prefixCandidate.contains("\t")) {
                if (expectedPrefix != null) {
                    if (!prefixCandidate.equals(expectedPrefix)) {
                        name = prefixCandidate;
                        if (split.length > 1) meta = MouseNumbers.tryParseInt(split[1], 0);
                        return create(expectedPrefix, name, meta);
                    }
                } else expectedPrefix = prefixCandidate;
            }

            if (split.length > 1) {
                String rawName = MouseStrings.trimWith(split[1], true, '\t');
                if (!rawName.isEmpty() && !rawName.contains(" ") && !rawName.contains("\t")) name = rawName;

                if (split.length > 2) meta = MouseNumbers.tryParseInt(split[2], 0);
            }
        }

        if (name != null && expectedPrefix != null) return create(expectedPrefix, name, meta);
        throw new ValueFormatException();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MinecraftItem)) return false;
        MinecraftItem minecraftBlock = (MinecraftItem) o;
        return meta == minecraftBlock.meta
                && Objects.equals(prefix, minecraftBlock.prefix)
                && Objects.equals(name, minecraftBlock.name);
    }

    @Override public String toString() { return asString(); }
    @Override public int hashCode()    { return Objects.hash(prefix, name, meta); }

    @Override
    public int compareTo(MinecraftItem o) {
        return Comparator
                .comparing(MinecraftItem::getPrefix)
                .thenComparing(MinecraftItem::getName)
                .thenComparing(MinecraftItem::getMeta)
                .compare(this, o);
    }
}