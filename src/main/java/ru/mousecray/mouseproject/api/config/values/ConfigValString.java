/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mouseproject.api.config.values;

import ru.mousecray.mouseproject.api.VariableValue;
import ru.mousecray.mouseproject.api.config.specific.ConfigValType;
import ru.mousecray.mouseproject.api.config.utils.Constraint;
import ru.mousecray.mouseproject.api.config.utils.PredefinedValue;
import ru.mousecray.mouseproject.api.config.values.base.ConfigOtherVal;
import ru.mousecray.mouseproject.api.customtype.CustomType;
import ru.mousecray.mouseproject.api.customtype.values.StringType;
import ru.mousecray.mouseproject.api.error.ValueFormatException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ConfigValString extends ConfigOtherVal<StringType> {
    @SafeVarargs
    public ConfigValString(
            @Nullable StringType defaultValue, @Nullable StringType disabledValue,
            @Nullable String specificDataType,
            @Nullable Constraint<?>[] constraints,
            @Nullable PredefinedValue<StringType>... configureValues
    ) {
        super(
                ConfigValType.STRING,
                defaultValue == null ? StringType.NULL : defaultValue,
                disabledValue == null ? StringType.NULL : disabledValue,
                specificDataType, constraints, configureValues
        );
    }

    public String getString()                     { return getValue().getValue(); }
    public void setString(@Nullable String value) { setValue(value == null ? StringType.NULL : StringType.create(value)); }

    @Nonnull @Override
    protected VariableValue<StringType> parseValue(@Nullable String value) {
        try {
            return VariableValue.create(CustomType.parse(StringType.class, value));
        } catch (ValueFormatException ignore) { return VariableValue.create(); }
    }

    @Override protected boolean saveDisabledValue() { return true; }
}