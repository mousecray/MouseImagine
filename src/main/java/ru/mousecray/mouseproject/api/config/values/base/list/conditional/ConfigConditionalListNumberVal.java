package ru.mousecray.mouseproject.api.config.values.base.list.conditional;

import ru.mousecray.mouseproject.api.VariableValue;
import ru.mousecray.mouseproject.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mouseproject.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mouseproject.api.config.ISupportRange;
import ru.mousecray.mouseproject.api.config.IValType;
import ru.mousecray.mouseproject.api.config.utils.Constraint;
import ru.mousecray.mouseproject.api.config.utils.PredefinedValue;
import ru.mousecray.mouseproject.api.customtype.NumberType;
import ru.mousecray.mouseproject.api.customtype.range.RangeContainer;
import ru.mousecray.mouseproject.api.customtype.values.ConditionalListType;
import ru.mousecray.mouseproject.api.customtype.values.ConditionalValType;
import ru.mousecray.mouseproject.api.log.ConsoleColor;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.Function;

import static ru.mousecray.mouseproject.api.VariableValue.create;


@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
@FieldsAreNonnullByDefault
public abstract class ConfigConditionalListNumberVal<
        T extends NumberType<?>
        > extends ConfigConditionalListVal<T> implements ISupportRange<T> {
    private final RangeContainer<T> range;
    @SafeVarargs
    public ConfigConditionalListNumberVal(
            IValType listComponentType, Class<T> listTypeClass,
            @Nullable Function<String, T> valCreator,
            ConditionalListType<T> defaultValue, ConditionalListType<T> disabledValue,
            RangeContainer<T> range,
            @Nullable Constraint<?>[] constraints,
            @Nullable PredefinedValue<ConditionalListType<T>>... configureValues
    ) {
        super(
                listComponentType, listTypeClass, valCreator,
                checkValue(range, defaultValue, "defaultValue"),
                checkValue(range, disabledValue, "disabledValue"),
                constraints, configureValues
        );
        this.range = Objects.requireNonNull(range);
    }

    private static <T extends NumberType<?>> ConditionalListType<T> checkValue(
            RangeContainer<T> range, ConditionalListType<T> value, String valueName
    ) {
        value.forEach(val -> {
            if (!range.isInRange(val.getValue())) {
                throw new IllegalStateException("ConfigValueSimpleListNumber got " + valueName +
                        " that contains listValue that not in valid range");
            }
        });
        return value;
    }

    @Override public boolean isInRange(T other)     { return range.isInRange(other); }
    @Override public RangeContainer<T> getRange()   { return range; }
    @Override public VariableValue<T> getMinValue() { return range.isEmpty() ? create() : create(range.getMinValue()); }
    @Override public VariableValue<T> getMaxValue() { return range.isEmpty() ? create() : create(range.getMaxValue()); }

    @SuppressWarnings("DataFlowIssue") @Override
    protected ConditionalValType<T> adaptListValue(ConditionalValType<T> value) {
        if (!isInRange(value.getValue())) {
            if (hasLogger()) {
                getLogger().warn("ConfigValueSimpleListNumber \"" + owner.getFullInternalName() +
                                "\" got value that not valid. It was adapted",
                        "Config", ConsoleColor.YELLOW_BG);
            }
            return value.createType(range.getNearValue(value.getValue()), value.isAnti());
        }
        return value;
    }
}