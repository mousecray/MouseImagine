/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mouseproject.api.customtype.values;

import ru.mousecray.mouseproject.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mouseproject.api.customtype.*;
import ru.mousecray.mouseproject.api.error.UnsupportedValException;
import ru.mousecray.mouseproject.api.error.ValueFormatException;
import ru.mousecray.mouseproject.api.utils.MouseStrings;
import ru.mousecray.mouseproject.api.utils.MouseUtils;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@SuppressWarnings({ "unchecked" })
@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public final class ConditionalValType<T extends CustomType<?>> extends CustomType<ConditionalValType<T>> {
    public static <T extends CustomType<?>> ConditionalValType<T> NULL() {
        return new ConditionalValType<>(null, false);
    }

    private final T       value;
    private final boolean anti;

    private ConditionalValType(@Nullable T value, boolean anti) {
        super();
        this.value = checkValue(value);
        this.anti = anti;
    }

    public static <T extends CustomType<?>> ConditionalValType<T> create(T value, PlusMinusType isAnti) {
        return new ConditionalValType<>(Objects.requireNonNull(value), isAnti.asBoolean());
    }

    private static <T extends CustomType<?>> ConditionalValType<T> create(T value, boolean isAnti) {
        return new ConditionalValType<>(Objects.requireNonNull(value), isAnti);
    }

    public static <T extends CustomType<?>> ConditionalValType<T> create(T value) {
        return create(value, PlusMinusType.FALSE);
    }

    @Nullable
    private static <T extends CustomType<?>> T checkValue(@Nullable T value) {
        if (value instanceof ConditionalValType) throw new UnsupportedValException("Enclosing ConditionValTypes is not support");
        return value;
    }

    public PlusMinusType isNull() { return PlusMinusType.create(value == null); }
    public T getValue()           { return value; }
    public PlusMinusType isAnti() { return PlusMinusType.create(anti); }

    public static <T extends CustomType<?>> ConditionalValType<T> fromString(Class<T> valClass, String raw) {
        raw = MouseStrings.trimWith(raw, true, '\t');
        if (raw == null) throw new ValueFormatException();
        if (raw.isEmpty()) return NULL();
        boolean anti = false;
        int     i    = raw.indexOf('!');
        if (i > -1) {
            raw = raw.substring(i);
            anti = true;
        }
        return create(CustomType.parse(valClass, raw), PlusMinusType.create(anti));
    }

    @Override public ListType<?, ?> asListType()    { throw new UnsupportedOperationException(); }
    @Override public LogicalType<?> asLogicalType() { return value.asLogicalType(); }
    @Override public NumberType<?> asNumberType()   { return value.asNumberType(); }
    @Override public OtherType<?> asOtherType()     { return value.asOtherType(); }

    public ConditionalValType<T> createType(CustomType<?> value, PlusMinusType isAnti) {
        return (ConditionalValType<T>)
                create(this.value.getCastPipeline().fromValue(value), isAnti);
    }

    private ConditionalValType<T> createType(CustomType<?> value, boolean isAnti) {
        return (ConditionalValType<T>)
                create(this.value.getCastPipeline().fromValue(value), isAnti);
    }

    @Override
    protected CustomCast<ConditionalValType<T>> createCastPipeline() {
        return new CustomCast<ConditionalValType<T>>() {
            @Override
            public ConditionalValType<T> fromValue(CustomType<?> other) {
                return createType(value.getCastPipeline().fromValue(other), PlusMinusType.FALSE);
            }

            @Override
            public <TYPE extends CustomType<TYPE>> TYPE asValue(TYPE other) {
                return value.getCastPipeline().asValue(other);
            }
        };
    }

    @Override
    protected CustomLogic<ConditionalValType<T>> createLogicPipeline() {
        return new CustomLogic<ConditionalValType<T>>() {
            @Override public boolean isLess(CustomType<?> other)        { return value.getLogicPipeline().isLess(other); }
            @Override public boolean isMore(CustomType<?> other)        { return value.getLogicPipeline().isMore(other); }
            @Override public boolean isEqual(CustomType<?> other)       { return value.getLogicPipeline().isEqual(other); }
            @Override public boolean isLessOrEqual(CustomType<?> other) { return value.getLogicPipeline().isLessOrEqual(other); }
            @Override public boolean isMoreOrEqual(CustomType<?> other) { return value.getLogicPipeline().isMoreOrEqual(other); }
            @Override public ConditionalValType<T> not()                { return create(value, !anti); }

            @Override
            public ConditionalValType<T> and(CustomType<?> other) {
                return createType(value.getLogicPipeline().and(other), anti);
            }

            @Override public ConditionalValType<T> or(CustomType<?> other) {
                return createType(value.getLogicPipeline().or(other), anti);
            }
        };
    }

    @Override
    protected CustomArithmetic<ConditionalValType<T>> createArithmeticPipeline() {
        return new CustomArithmetic<ConditionalValType<T>>() {
            @Override public ConditionalValType<T> invert()    { return create(value, !anti); }
            @Override public ConditionalValType<T> increment() { return invert(); }
            @Override public ConditionalValType<T> decrement() { return invert(); }

            @Override
            public ConditionalValType<T> plus(CustomType<?> other) {
                return createType(value.getArithmeticPipeline().plus(other), anti);
            }

            @Override
            public ConditionalValType<T> minus(CustomType<?> other) {
                return createType(value.getArithmeticPipeline().minus(other), anti);
            }

            @Override
            public ConditionalValType<T> divide(CustomType<?> other) {
                return createType(value.getArithmeticPipeline().divide(other), anti);
            }

            @Override
            public ConditionalValType<T> multiply(CustomType<?> other) {
                return createType(value.getArithmeticPipeline().multiply(other), anti);
            }

            @Override
            public ConditionalValType<T> modulo(CustomType<?> other) {
                return createType(value.getArithmeticPipeline().modulo(other), anti);
            }
        };
    }

    @Override
    protected CustomBitwise<ConditionalValType<T>> createBitwisePipeline() {
        return new CustomBitwise<ConditionalValType<T>>() {
            @Override
            public ConditionalValType<T> and(CustomType<?> other) {
                return createType(value.getBitwisePipeline().and(other), anti);
            }

            @Override
            public ConditionalValType<T> or(CustomType<?> other) {
                return createType(value.getBitwisePipeline().or(other), anti);
            }

            @Override
            public ConditionalValType<T> xor(CustomType<?> other) {
                return createType(value.getBitwisePipeline().xor(other), anti);
            }

            @Override
            public ConditionalValType<T> not() {
                return createType(value.getBitwisePipeline().not(), anti);
            }

            @Override
            public ConditionalValType<T> leftShift(int other) {
                return createType(value.getBitwisePipeline().leftShift(other), anti);
            }

            @Override
            public ConditionalValType<T> rightShift(int other) {
                return createType(value.getBitwisePipeline().rightShift(other), anti);
            }

            @Override
            public ConditionalValType<T> uRightShift(int other) {
                return createType(value.getBitwisePipeline().uRightShift(other), anti);
            }
        };
    }

    @Override public String toString() { return anti ? '!' + value.toString() : value.toString(); }
    @Override public int hashCode()    { return Objects.hash(super.hashCode(), anti); }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") @Override
    public boolean equals(Object o) {
        return MouseUtils.Equator
                .<ConditionalValType<T>, T>equaling(ConditionalValType::getValue)
                .thenEqualing(ConditionalValType::isAnti)
                .equals(getClass(), this, o);
    }

    @Override
    public int compareTo(ConditionalValType<T> o) {
        int result = ((CustomType) value).compareTo(o.getValue());
        if (result == 0 && anti) result = -1;
        return result;
    }
}