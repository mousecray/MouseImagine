package ru.mousecray.mouseproject.api.customtype.values;

import ru.mousecray.mouseproject.api.customtype.LogicalType;
import ru.mousecray.mouseproject.api.customtype.NumberType;
import ru.mousecray.mouseproject.api.error.ValueFormatException;
import ru.mousecray.mouseproject.api.utils.MouseNumbers;
import ru.mousecray.mouseproject.api.utils.MouseStrings;

import javax.annotation.Nonnull;

public final class DecimalType extends NumberType<Double> {
    public static final DecimalType MIN  = new DecimalType(Double.MIN_VALUE);
    public static final DecimalType MAX  = new DecimalType(Double.MAX_VALUE);
    public static final DecimalType NULL = new DecimalType(0D);

    static {
        storage.put(DecimalType.class, str -> {
            str = MouseStrings.trimWith(str, true, '\t');
            if (str == null || str.isEmpty()) throw new ValueFormatException();
            Double v = MouseNumbers.tryParseDouble(str);
            if (v != null) return create(v);
            throw new ValueFormatException();
        });
    }

    private DecimalType(double value) { super(value); }

    public static DecimalType create(double value) {
        if (MIN.asNumber() >= value) return MIN;
        else if (MAX.asNumber() <= value) return MAX;
        else if (value == 0) return NULL;
        else return new DecimalType(value);
    }

    public static DecimalType calcMedium(@Nonnull DecimalType min, @Nonnull DecimalType max) {
        return create(min.value + max.value / 2D);
    }

    public static DecimalType calcLow(@Nonnull DecimalType min, @Nonnull DecimalType max) {
        return create((min.value + (min.value + max.value) / 2D) / 2D);
    }

    public static DecimalType calcHigh(@Nonnull DecimalType min, @Nonnull DecimalType max) {
        return create((max.value + (min.value + max.value) / 2D) / 2D);
    }

    @Nonnull @Override public LogicalType<?> asLogicalType() { return PlusMinusType.create(value > 0); }
    public IntegralType asIntegralType()                     { return IntegralType.create(value.longValue()); }
    public PercentType asPercentType()                       { return PercentType.create(value); }
    public StringType asStringType()                         { return StringType.create(toString()); }

    public RandomQuantityType asRandomQuantityType() {
        return RandomQuantityType.create(asPercentType(), IntegralType.NULL, IntegralType.NULL);
    }

    @Nonnull @Override public Double asNumber() { return value; }

    @SuppressWarnings("unchecked") @Nonnull @Override
    public DecimalType createType(@Nonnull Number value) {
        return create(value.doubleValue());
    }
}