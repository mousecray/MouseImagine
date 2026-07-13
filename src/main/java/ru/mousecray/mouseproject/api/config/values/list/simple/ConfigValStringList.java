package ru.mousecray.mouseproject.api.config.values.list.simple;

import ru.mousecray.mouseproject.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mouseproject.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mouseproject.api.config.specific.ConfigValType;
import ru.mousecray.mouseproject.api.config.utils.Constraint;
import ru.mousecray.mouseproject.api.config.utils.PredefinedValue;
import ru.mousecray.mouseproject.api.config.values.base.list.simple.ConfigSimpleListOtherVal;
import ru.mousecray.mouseproject.api.customtype.CustomType;
import ru.mousecray.mouseproject.api.customtype.values.SimpleListType;
import ru.mousecray.mouseproject.api.customtype.values.StringType;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
@FieldsAreNonnullByDefault
public class ConfigValStringList extends ConfigSimpleListOtherVal<StringType> {
    @SafeVarargs
    public ConfigValStringList(
            @Nullable SimpleListType<StringType> defaultValue, @Nullable SimpleListType<StringType> disabledValue,
            @Nullable Constraint<?>[] constraints,
            @Nullable PredefinedValue<SimpleListType<StringType>>... configureValues
    ) {
        super(
                ConfigValType.STRING, StringType.class, createValCreator(),
                defaultValue == null
                        ? SimpleListType.create(StringType.class, createValCreator())
                        : defaultValue,
                disabledValue == null
                        ? SimpleListType.create(StringType.class, createValCreator())
                        : disabledValue,
                constraints, configureValues
        );
    }

    private static Function<String, StringType> createValCreator() {
        return str -> CustomType.parse(StringType.class, str);
    }
}