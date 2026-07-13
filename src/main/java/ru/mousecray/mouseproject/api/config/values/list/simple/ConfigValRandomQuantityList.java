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
import ru.mousecray.mouseproject.api.customtype.values.RandomQuantityType;
import ru.mousecray.mouseproject.api.customtype.values.SimpleListType;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
@FieldsAreNonnullByDefault
public class ConfigValRandomQuantityList extends ConfigSimpleListNumberVal<RandomQuantityType> {
    @SafeVarargs
    public ConfigValRandomQuantityList(
            @Nullable SimpleListType<RandomQuantityType> defaultValue, @Nullable SimpleListType<RandomQuantityType> disabledValue,
            @Nullable RangeContainer<RandomQuantityType> range,
            @Nullable Constraint<?>[] constraints,
            @Nullable PredefinedValue<SimpleListType<RandomQuantityType>>... configureValues
    ) {
        super(
                ConfigValType.RANDOM_QUANTITY, RandomQuantityType.class, createValCreator(),
                defaultValue == null
                        ? SimpleListType.create(RandomQuantityType.class, createValCreator())
                        : defaultValue,
                disabledValue == null
                        ? SimpleListType.create(RandomQuantityType.class, createValCreator())
                        : disabledValue,
                range == null
                        ? new RangeContainer<>(Range.of(RandomQuantityType.MIN, RandomQuantityType.MAX))
                        : range,
                constraints, configureValues
        );
    }

    private static Function<String, RandomQuantityType> createValCreator() {
        return str -> CustomType.parse(RandomQuantityType.class, str);
    }
}