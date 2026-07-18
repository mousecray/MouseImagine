/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mouseproject.api.customtype.values;

import ru.mousecray.mouseproject.api.customtype.LogicalType;
import ru.mousecray.mouseproject.api.customtype.NumberType;
import ru.mousecray.mouseproject.api.error.ValueFormatException;
import ru.mousecray.mouseproject.api.utils.MouseStrings;
import ru.mousecray.mouseproject.api.utils.MouseUtils;

import javax.annotation.Nonnull;
import java.util.Objects;

import static ru.mousecray.mouseproject.api.utils.MouseNumbers.tryParseDouble;
import static ru.mousecray.mouseproject.api.utils.MouseStrings.subAndTrimWithTabs;
import static ru.mousecray.mouseproject.api.utils.MouseStrings.trimWith;

public final class RandomQuantityType extends NumberType<Double> {
    public static final RandomQuantityType MIN  = new RandomQuantityType(PercentType.MIN, IntegralType.NULL, IntegralType.NULL);
    public static final RandomQuantityType MAX  = new RandomQuantityType(PercentType.MAX, IntegralType.MAX, IntegralType.MAX);
    public static final RandomQuantityType NULL = new RandomQuantityType(PercentType.NULL, IntegralType.NULL, IntegralType.NULL);

    static {
        storage.put(RandomQuantityType.class, str -> {
            str = MouseStrings.trimWith(str, true, '\t');
            if (str == null || str.isEmpty()) throw new ValueFormatException();

            Double min    = null, max = null;
            Double chance = null;

            String[] split = str.split(":");
            if (split.length > 0) {
                String[] split2 = split[0].split("-");
                if (split2.length > 0) min = tryParseDouble(trimWith(split2[0], true, '\t'));
                if (split2.length > 1) max = tryParseDouble(trimWith(split2[1], true, '\t'));

                if (split.length > 1) chance = tryParseDouble(subAndTrimWithTabs(split[1], null, "%"));
            }

            if (min == null) min = 0D;
            if (max == null) max = min;
            if (chance == null) throw new ValueFormatException();

            return create(
                    PercentType.create(chance),
                    IntegralType.create(min.longValue()),
                    IntegralType.create(max.longValue())
            );
        });
    }

    private final int min;
    private final int max;

    private RandomQuantityType(PercentType chance, IntegralType min, IntegralType max) {
        super(chance.asDouble());
        this.min = min.asInt();
        this.max = max.asInt();
    }

    public static RandomQuantityType create(PercentType chance, IntegralType min, IntegralType max) {
        if (chance.getLogicPipeline().isLessOrEqual(MIN.getChance())
                && min.getLogicPipeline().isLessOrEqual(MIN.getMin())
                && max.getLogicPipeline().isLessOrEqual(MIN.getMax())
        ) return MIN;
        else if (chance.getLogicPipeline().isMoreOrEqual(MAX.getChance())
                && min.getLogicPipeline().isMoreOrEqual(MIN.getMin())
                && max.getLogicPipeline().isMoreOrEqual(MIN.getMax())) return MAX;
        else if (chance.getLogicPipeline().isEqual(NULL.getChance())
                && min.getLogicPipeline().isMoreOrEqual(MIN.getMin())
                && max.getLogicPipeline().isMoreOrEqual(MIN.getMax())) return NULL;
        else return new RandomQuantityType(chance, min, max);
    }

    public static RandomQuantityType calcMedium(@Nonnull RandomQuantityType min, @Nonnull RandomQuantityType max) {
        return create(
                PercentType.create(min.value + max.value / 2D),
                IntegralType.create((long) (min.min + max.min / 2D)),
                IntegralType.create((long) (min.max + max.max / 2D))
        );
    }

    public static RandomQuantityType calcLow(@Nonnull RandomQuantityType min, @Nonnull RandomQuantityType max) {
        return create(
                PercentType.create((min.value + (min.value + max.value) / 2D) / 2D),
                IntegralType.create((long) (min.min + (min.min + max.min / 2D) / 2D)),
                IntegralType.create((long) (min.max + (min.max + max.max / 2D) / 2D))
        );
    }

    public static RandomQuantityType calcHigh(@Nonnull RandomQuantityType min, @Nonnull RandomQuantityType max) {
        return create(
                PercentType.create((max.value + (min.value + max.value) / 2D) / 2D),
                IntegralType.create((long) (max.min + (min.min + max.min / 2D) / 2D)),
                IntegralType.create((long) (max.max + (min.max + max.max / 2D) / 2D))
        );
    }

    public PercentType getChance()                           { return PercentType.create(value); }
    public IntegralType getMin()                             { return IntegralType.create(min); }
    public IntegralType getMax()                             { return IntegralType.create(max); }

    @Nonnull @Override public LogicalType<?> asLogicalType() { return PlusMinusType.create(value > 0); }
    public DecimalType asDecimalType()                       { return DecimalType.create(value); }
    public IntegralType asIntegralType()                     { return IntegralType.create(value.longValue()); }
    public PercentType asPercentType()                       { return getChance(); }
    public StringType asStringType()                         { return StringType.create(toString()); }

    @Nonnull @Override public Double asNumber()              { return value; }

    @SuppressWarnings("unchecked") @Nonnull @Override
    public RandomQuantityType createType(@Nonnull Number value) {
        return create(PercentType.create(value.doubleValue()), IntegralType.NULL, IntegralType.NULL);
    }

    @Nonnull @Override
    public String toString() {
        return String.format("%s-%s:%s%%", getMin().toString(), getMax().toString(), super.toString());
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") @Override
    public boolean equals(Object o) {
        return MouseUtils.Equator
                .equaling(RandomQuantityType::getChance)
                .thenEqualing(RandomQuantityType::getMin)
                .thenEqualing(RandomQuantityType::getMax)
                .equals(getClass(), this, o);
    }

    @Override public int hashCode()                          { return Objects.hash(value, min, max); }
    @Override public int compareTo(@Nonnull NumberType<?> o) { return Double.compare(value, o.asDouble()); }
}