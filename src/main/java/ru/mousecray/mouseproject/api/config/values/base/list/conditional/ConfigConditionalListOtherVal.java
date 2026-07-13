package ru.mousecray.mouseproject.api.config.values.base.list.conditional;

import ru.mousecray.mouseproject.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mouseproject.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mouseproject.api.config.IValType;
import ru.mousecray.mouseproject.api.config.utils.Constraint;
import ru.mousecray.mouseproject.api.config.utils.PredefinedValue;
import ru.mousecray.mouseproject.api.customtype.OtherType;
import ru.mousecray.mouseproject.api.customtype.values.ConditionalListType;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
@FieldsAreNonnullByDefault
public abstract class ConfigConditionalListOtherVal<T extends OtherType<?>> extends ConfigConditionalListVal<T> {
    @SafeVarargs
    public ConfigConditionalListOtherVal(
            IValType listComponentType, Class<T> listTypeClass,
            @Nullable Function<String, T> valCreator,
            ConditionalListType<T> defaultValue, ConditionalListType<T> disabledValue,
            @Nullable Constraint<?>[] constraints,
            @Nullable PredefinedValue<ConditionalListType<T>>... configureValues
    ) {
        super(
                listComponentType, listTypeClass, valCreator, defaultValue, disabledValue,
                constraints, configureValues
        );
    }
}