package ru.mousecray.mouseproject.api.config.values.base.list.simple;

import ru.mousecray.mouseproject.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mouseproject.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mouseproject.api.config.specific.ConfigValType;
import ru.mousecray.mouseproject.api.config.utils.Constraint;
import ru.mousecray.mouseproject.api.config.utils.PredefinedValue;
import ru.mousecray.mouseproject.api.customtype.LogicalType;
import ru.mousecray.mouseproject.api.customtype.values.SimpleListType;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
@FieldsAreNonnullByDefault
public abstract class ConfigSimpleListLogicalVal<T extends LogicalType<?>> extends ConfigSimpleListVal<T> {
    @SafeVarargs
    public ConfigSimpleListLogicalVal(
            Class<T> listTypeClass,
            @Nullable Function<String, T> valCreator,
            SimpleListType<T> defaultValue, SimpleListType<T> disabledValue,
            @Nullable Constraint<?>[] constraints,
            @Nullable PredefinedValue<SimpleListType<T>>... configureValues
    ) {
        super(
                ConfigValType.LOGICAL, listTypeClass, valCreator, defaultValue, disabledValue,
                constraints, configureValues
        );
    }
}