package ru.mousecray.mouseproject.api.config.values.list.conditional;

import ru.mousecray.mouseproject.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mouseproject.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mouseproject.api.config.specific.ConfigValType;
import ru.mousecray.mouseproject.api.config.utils.Constraint;
import ru.mousecray.mouseproject.api.config.utils.PredefinedValue;
import ru.mousecray.mouseproject.api.config.values.base.list.conditional.ConfigConditionalListNumberVal;
import ru.mousecray.mouseproject.api.customtype.CustomType;
import ru.mousecray.mouseproject.api.customtype.range.Range;
import ru.mousecray.mouseproject.api.customtype.range.RangeContainer;
import ru.mousecray.mouseproject.api.customtype.values.ConditionalListType;
import ru.mousecray.mouseproject.api.customtype.values.PercentType;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
@FieldsAreNonnullByDefault
public class ConfigValPercentConditionalList extends ConfigConditionalListNumberVal<PercentType> {
    @SafeVarargs
    public ConfigValPercentConditionalList(
            @Nullable ConditionalListType<PercentType> defaultValue, @Nullable ConditionalListType<PercentType> disabledValue,
            @Nullable RangeContainer<PercentType> range,
            @Nullable Constraint<?>[] constraints,
            @Nullable PredefinedValue<ConditionalListType<PercentType>>... configureValues
    ) {
        super(
                ConfigValType.PERCENT, PercentType.class, createValCreator(),
                defaultValue == null
                        ? ConditionalListType.create(PercentType.class, createValCreator())
                        : defaultValue,
                disabledValue == null
                        ? ConditionalListType.create(PercentType.class, createValCreator())
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