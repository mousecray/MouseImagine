package ru.mousecray.mouseproject.api.config.values.list.simple;

import ru.mousecray.mouseproject.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mouseproject.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mouseproject.api.config.utils.Constraint;
import ru.mousecray.mouseproject.api.config.utils.PredefinedValue;
import ru.mousecray.mouseproject.api.config.values.base.list.simple.ConfigSimpleListLogicalVal;
import ru.mousecray.mouseproject.api.customtype.CustomType;
import ru.mousecray.mouseproject.api.customtype.values.PlusMinusType;
import ru.mousecray.mouseproject.api.customtype.values.SimpleListType;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
@FieldsAreNonnullByDefault
public class ConfigValPlusMinusList extends ConfigSimpleListLogicalVal<PlusMinusType> {
    @SafeVarargs
    public ConfigValPlusMinusList(
            @Nullable SimpleListType<PlusMinusType> defaultValue, @Nullable SimpleListType<PlusMinusType> disabledValue,
            @Nullable Constraint<?>[] constraints,
            @Nullable PredefinedValue<SimpleListType<PlusMinusType>>... configureValues
    ) {
        super(
                PlusMinusType.class, createValCreator(),
                defaultValue == null
                        ? SimpleListType.create(PlusMinusType.class, createValCreator())
                        : defaultValue,
                disabledValue == null
                        ? SimpleListType.create(PlusMinusType.class, createValCreator())
                        : disabledValue,
                constraints, configureValues
        );
    }

    private static Function<String, PlusMinusType> createValCreator() {
        return str -> CustomType.parse(PlusMinusType.class, str);
    }
}