/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mouseproject.api.config.values.list.simple;

import ru.mousecray.mouseproject.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mouseproject.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mouseproject.api.config.specific.ConfigValType;
import ru.mousecray.mouseproject.api.config.utils.Constraint;
import ru.mousecray.mouseproject.api.config.utils.PredefinedValue;
import ru.mousecray.mouseproject.api.config.values.base.list.simple.ConfigSimpleListNumberVal;
import ru.mousecray.mouseproject.api.customtype.CustomType;
import ru.mousecray.mouseproject.api.customtype.range.Range;
import ru.mousecray.mouseproject.api.customtype.range.RangeContainer;
import ru.mousecray.mouseproject.api.customtype.values.PercentType;
import ru.mousecray.mouseproject.api.customtype.values.SimpleListType;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
@FieldsAreNonnullByDefault
public class ConfigValPercentList extends ConfigSimpleListNumberVal<PercentType> {
    @SafeVarargs
    public ConfigValPercentList(
            @Nullable SimpleListType<PercentType> defaultValue, @Nullable SimpleListType<PercentType> disabledValue,
            @Nullable RangeContainer<PercentType> range,
            @Nullable Constraint<?>[] constraints,
            @Nullable PredefinedValue<SimpleListType<PercentType>>... configureValues
    ) {
        super(
                ConfigValType.PERCENT, PercentType.class, createValCreator(),
                defaultValue == null
                        ? SimpleListType.create(PercentType.class, createValCreator())
                        : defaultValue,
                disabledValue == null
                        ? SimpleListType.create(PercentType.class, createValCreator())
                        : disabledValue,
                range == null
                        ? new RangeContainer<>(Range.of(PercentType.MIN, PercentType.MAX))
                        : range,
                constraints, configureValues
        );
    }

    private static Function<String, PercentType> createValCreator() {
        return str -> CustomType.parse(PercentType.class, str);
    }
}