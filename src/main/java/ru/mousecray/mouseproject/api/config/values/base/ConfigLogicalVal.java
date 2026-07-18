/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mouseproject.api.config.values.base;

import ru.mousecray.mouseproject.api.config.ConfigVal;
import ru.mousecray.mouseproject.api.config.IValType;
import ru.mousecray.mouseproject.api.config.utils.Constraint;
import ru.mousecray.mouseproject.api.config.utils.PredefinedValue;
import ru.mousecray.mouseproject.api.customtype.LogicalType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class ConfigLogicalVal<T extends LogicalType<?>> extends ConfigVal<T> {
    @SafeVarargs
    public ConfigLogicalVal(
            IValType type, T defaultValue, T disabledValue, @Nullable String specificDataType,
            @Nullable Constraint<?>[] constraints, @Nullable PredefinedValue<T>... configureValues
    ) {
        super(type, defaultValue, disabledValue, val -> specificDataType, constraints, configureValues);
    }

    protected T getLogical()           { return getValue(); }
    public boolean isTrue()            { return getLogical().isTrue(); }
    public boolean isFalse()           { return getLogical().isFalse(); }

    protected void setLogical(T value) { setValue(value); }
    public void setTrue()              { setLogical(getLogical().createType(true)); }
    public void setFalse()             { setLogical(getLogical().createType(false)); }

    @SuppressWarnings("unchecked") @Nonnull @Override
    protected T processValueIfNotEqualConstraint(@Nonnull T value) {
        return (T) value.getLogicPipeline().not();
    }

    @Override protected boolean saveDisabledValue() { return false; }
}
