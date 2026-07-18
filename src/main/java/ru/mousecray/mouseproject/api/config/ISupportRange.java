/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mouseproject.api.config;

import ru.mousecray.mouseproject.api.VariableValue;
import ru.mousecray.mouseproject.api.config.specific.ConfigLocaleType;
import ru.mousecray.mouseproject.api.customtype.NumberType;
import ru.mousecray.mouseproject.api.customtype.range.RangeContainer;

public interface ISupportRange<T extends NumberType<?>> {
    boolean isInRange(T other);
    RangeContainer<T> getRange();
    VariableValue<T> getMinValue();
    VariableValue<T> getMaxValue();

    default ILocaleType getRangeLocaleType() { return ConfigLocaleType.RANGE; }
    default boolean saveRange()              { return true; }
}