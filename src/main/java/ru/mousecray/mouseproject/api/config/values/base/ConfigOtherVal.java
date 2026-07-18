/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mouseproject.api.config.values.base;

import ru.mousecray.mouseproject.api.config.ConfigVal;
import ru.mousecray.mouseproject.api.config.IValType;
import ru.mousecray.mouseproject.api.config.utils.Constraint;
import ru.mousecray.mouseproject.api.config.utils.PredefinedValue;
import ru.mousecray.mouseproject.api.customtype.OtherType;

import javax.annotation.Nullable;

public abstract class ConfigOtherVal<T extends OtherType<?>> extends ConfigVal<T> {
    @SafeVarargs
    public ConfigOtherVal(
            IValType type, T defaultValue, T disabledValue, @Nullable String specificDataType,
            @Nullable Constraint<?>[] constraints, @Nullable PredefinedValue<T>... configureValues
    ) {
        super(type, defaultValue, disabledValue, val -> specificDataType, constraints, configureValues);
    }

    @Override protected boolean saveDisabledValue() { return false; }
}
