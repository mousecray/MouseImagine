package ru.mousecray.mouseproject.api.config.values.base.list.simple;

import ru.mousecray.mouseproject.api.VariableValue;
import ru.mousecray.mouseproject.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mouseproject.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mouseproject.api.config.IValType;
import ru.mousecray.mouseproject.api.config.specific.ConfigValType;
import ru.mousecray.mouseproject.api.config.utils.Constraint;
import ru.mousecray.mouseproject.api.config.utils.PredefinedValue;
import ru.mousecray.mouseproject.api.config.values.base.ConfigListVal;
import ru.mousecray.mouseproject.api.customtype.CustomType;
import ru.mousecray.mouseproject.api.customtype.values.SimpleListType;
import ru.mousecray.mouseproject.api.error.ValueFormatException;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
@FieldsAreNonnullByDefault
public abstract class ConfigSimpleListVal<T extends CustomType<?>> extends ConfigListVal<T, T, SimpleListType<T>> {
    @SafeVarargs
    public ConfigSimpleListVal(
            IValType listComponentType, Class<T> listTypeClass,
            @Nullable Function<String, T> valCreator,
            SimpleListType<T> defaultValue, SimpleListType<T> disabledValue,
            @Nullable Constraint<?>[] constraints,
            @Nullable PredefinedValue<SimpleListType<T>>... configureValues
    ) {
        super(
                ConfigValType.LIST, defaultValue, disabledValue, listComponentType,
                listTypeClass, valCreator, constraints, configureValues
        );
    }

    @Override
    protected VariableValue<SimpleListType<T>> parseValue(@Nullable String value) {
        try {
            return VariableValue.create(
                    SimpleListType.fromString(
                            listComponentClass, value, valCreator,
                            hasLogger() ? getLogger() : null
                    )
            );
        } catch (ValueFormatException ignore) { return VariableValue.create(); }
    }
}