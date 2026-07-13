package ru.mousecray.mouseproject.api.config.values.base.list.simple;

import ru.mousecray.mouseproject.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mouseproject.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mouseproject.api.config.IValType;
import ru.mousecray.mouseproject.api.config.utils.Constraint;
import ru.mousecray.mouseproject.api.config.utils.PredefinedValue;
import ru.mousecray.mouseproject.api.customtype.OtherType;
import ru.mousecray.mouseproject.api.customtype.values.SimpleListType;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
@FieldsAreNonnullByDefault
public abstract class ConfigSimpleListOtherVal<T extends OtherType<?>> extends ConfigSimpleListVal<T> {
    @SafeVarargs
    public ConfigSimpleListOtherVal(
            IValType listComponentType, Class<T> listTypeClass,
            @Nullable Function<String, T> valCreator,
            SimpleListType<T> defaultValue, SimpleListType<T> disabledValue,
            @Nullable Constraint<?>[] constraints,
            @Nullable PredefinedValue<SimpleListType<T>>... configureValues
    ) {
        super(
                listComponentType, listTypeClass, valCreator, defaultValue, disabledValue,
                constraints, configureValues
        );
    }
}