/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mouseproject.api.customtype.values;

import ru.mousecray.mouseproject.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mouseproject.api.customtype.OtherType;
import ru.mousecray.mouseproject.api.error.ValueFormatException;
import ru.mousecray.mouseproject.api.utils.MouseNumbers;
import ru.mousecray.mouseproject.api.utils.MouseStrings;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public final class StringType extends OtherType<String> {
    public static final StringType NULL = new StringType("");

    static {
        storage.put(StringType.class, str -> {
            str = MouseStrings.trimWith(str, true, '\t');
            return str == null || str.isEmpty() ? NULL : create(str);
        });
    }

    private StringType(String value)              { super(Objects.requireNonNull(value)); }
    public static StringType create(String value) { return new StringType(value); }

    public String asString()                      { return value; }

    private Number asNumber() {
        Double v = MouseNumbers.tryParseDouble(value);
        if (v == null) throw new ValueFormatException();
        return v;
    }

    public DecimalType asDecimalType()   { return DecimalType.create(asNumber().doubleValue()); }
    public IntegralType asIntegralType() { return IntegralType.create(asNumber().longValue()); }
    public PercentType asPercentType()   { return PercentType.create(asNumber().doubleValue()); }

    public RandomQuantityType asRandomQuantityType() {
        return RandomQuantityType.create(asPercentType(), IntegralType.NULL, IntegralType.NULL);
    }

    @SuppressWarnings("unchecked") @Override
    public StringType createType(Object value) {
        return create(value.toString());
    }
}