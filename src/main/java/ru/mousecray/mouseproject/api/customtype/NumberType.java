package ru.mousecray.mouseproject.api.customtype;

import ru.mousecray.mouseproject.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mouseproject.api.error.UnsupportedValException;
import ru.mousecray.mouseproject.api.utils.MouseLogic;
import ru.mousecray.mouseproject.api.utils.MouseNumbers;
import ru.mousecray.mouseproject.api.utils.MouseUtils;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Comparator;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public abstract class NumberType<T extends Comparable<T>> extends CustomType<NumberType<?>> {
    protected final T value;

    protected NumberType(T value) {
        super(CustomValType.NUMBER);
        this.value = value;
    }

    public T getValue()                             { return value; }

    @Override public ListType<?, ?> asListType()    { throw new UnsupportedOperationException(); }
    @Override public NumberType<?> asNumberType()   { return this; }
    @Override public LogicalType<?> asLogicalType() { throw new UnsupportedOperationException(); }
    @Override public OtherType<?> asOtherType()     { throw new UnsupportedOperationException(); }

    public abstract Number asNumber();

    public double asDouble()                        { return asNumber().doubleValue(); }
    public float asFloat()                          { return asNumber().floatValue(); }
    public long asLong()                            { return asNumber().longValue(); }
    public int asInt()                              { return asNumber().intValue(); }
    public short asShort()                          { return asNumber().shortValue(); }
    public byte asByte()                            { return asNumber().byteValue(); }

    public abstract <TYPE extends NumberType<?>> TYPE createType(Number value);

    @Override
    protected CustomCast<NumberType<?>> createCastPipeline() {
        return new CustomCast<NumberType<?>>() {
            @Override
            public NumberType<T> fromValue(CustomType<?> other) {
                if (other instanceof NumberType) {
                    NumberType<?> type = (NumberType<?>) other;
                    return createType(type.asNumber());
                }
                throw new UnsupportedValException();
            }

            @Override
            public <TYPE extends CustomType<TYPE>> TYPE asValue(TYPE other) {
                if (other instanceof NumberType) {
                    return (TYPE) ((NumberType<?>) other).createType(asNumber());
                }
                throw new UnsupportedValException();
            }
        };
    }

    @Override
    protected CustomLogic<NumberType<?>> createLogicPipeline() {
        return new CustomLogic<NumberType<?>>() {
            @Override
            public boolean isLess(CustomType<?> other) {
                if (other instanceof NumberType) {
                    return MouseLogic.isLess(asNumber(), ((NumberType<?>) other).asNumber());
                }
                throw new UnsupportedValException();
            }

            @Override
            public boolean isMore(CustomType<?> other) {
                if (other instanceof NumberType) {
                    return MouseLogic.isMore(asNumber(), ((NumberType<?>) other).asNumber());
                }
                throw new UnsupportedValException();
            }

            @Override
            public boolean isEqual(CustomType<?> other) {
                if (other instanceof NumberType) {
                    return MouseLogic.isEqual(asNumber(), ((NumberType<?>) other).asNumber());
                }
                throw new UnsupportedValException();
            }

            @Override
            public boolean isLessOrEqual(CustomType<?> other) {
                if (other instanceof NumberType) {
                    return MouseLogic.isLessOrEqual(asNumber(), ((NumberType<?>) other).asNumber());
                }
                throw new UnsupportedValException();
            }

            @Override
            public boolean isMoreOrEqual(CustomType<?> other) {
                if (other instanceof NumberType) {
                    return MouseLogic.isMoreOrEqual(asNumber(), ((NumberType<?>) other).asNumber());
                }
                throw new UnsupportedValException();
            }

            @Override public NumberType<T> not()                    { throw new UnsupportedOperationException(); }
            @Override public NumberType<T> and(CustomType<?> other) { throw new UnsupportedOperationException(); }
            @Override public NumberType<T> or(CustomType<?> other)  { throw new UnsupportedOperationException(); }
        };
    }

    @Override
    protected CustomArithmetic<NumberType<?>> createArithmeticPipeline() {
        return new CustomArithmetic<NumberType<?>>() {
            @Override public NumberType<T> invert()    { return createType(MouseNumbers.invert(asNumber())); }
            @Override public NumberType<T> increment() { return createType(asNumber().doubleValue() + 1); }
            @Override public NumberType<T> decrement() { return createType(asNumber().doubleValue() - 1); }

            @Override
            public NumberType<T> plus(CustomType<?> other) {
                if (other instanceof NumberType) {
                    return createType(
                            MouseNumbers.plus(asNumber(), ((NumberType<?>) other).asNumber())
                    );
                }
                throw new UnsupportedValException();
            }

            @Override
            public NumberType<T> minus(CustomType<?> other) {
                if (other instanceof NumberType) {
                    return createType(
                            MouseNumbers.minus(asNumber(), ((NumberType<?>) other).asNumber())
                    );
                }
                throw new UnsupportedValException();
            }

            @Override
            public NumberType<T> divide(CustomType<?> other) {
                if (other instanceof NumberType) {
                    return createType(
                            MouseNumbers.divide(asNumber(), ((NumberType<?>) other).asNumber())
                    );
                }
                throw new UnsupportedValException();
            }

            @Override
            public NumberType<T> multiply(CustomType<?> other) {
                if (other instanceof NumberType) {
                    return createType(
                            MouseNumbers.multiply(asNumber(), ((NumberType<?>) other).asNumber())
                    );
                }
                throw new UnsupportedValException();
            }

            @Override
            public NumberType<T> modulo(CustomType<?> other) {
                if (other instanceof NumberType) {
                    return createType(
                            MouseNumbers.modulo(asNumber(), ((NumberType<?>) other).asNumber())
                    );
                }
                throw new UnsupportedValException();
            }
        };
    }

    @Override
    protected CustomBitwise<NumberType<?>> createBitwisePipeline() {
        return new CustomBitwise<NumberType<?>>() {
            @Override
            public NumberType<T> and(CustomType<?> other) {
                if (other instanceof NumberType) {
                    return createType(
                            MouseNumbers.and(asNumber(), ((NumberType<?>) other).asNumber())
                    );
                }
                throw new UnsupportedValException();
            }

            @Override
            public NumberType<T> or(CustomType<?> other) {
                if (other instanceof NumberType) {
                    return createType(
                            MouseNumbers.or(asNumber(), ((NumberType<?>) other).asNumber())
                    );
                }
                throw new UnsupportedValException();
            }

            @Override
            public NumberType<T> xor(CustomType<?> other) {
                if (other instanceof NumberType) {
                    return createType(
                            MouseNumbers.xor(asNumber(), ((NumberType<?>) other).asNumber())
                    );
                }
                throw new UnsupportedValException();
            }

            @Override public NumberType<T> not() { return createType(MouseNumbers.not(asNumber())); }

            @Override
            public NumberType<T> leftShift(int other) {
                return createType(MouseNumbers.leftShift(asNumber(), other));
            }

            @Override
            public NumberType<T> rightShift(int other) {
                return createType(MouseNumbers.rightShift(asNumber(), other));
            }

            @Override
            public NumberType<T> uRightShift(int other) {
                return createType(MouseNumbers.uRightShift(asNumber(), other));
            }
        };
    }

    @Override public int hashCode()    { return Objects.hash(value); }
    @Override public String toString() { return MouseNumbers.formatObjectIfNumber(value, false, true); }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") @Override
    public boolean equals(Object o) {
        return MouseUtils.Equator
                .<NumberType<T>, T>equaling(NumberType::getValue)
                .equals(getClass(), this, o);
    }

    @Override
    public int compareTo(@Nonnull NumberType<?> o) {
        return Comparator
                .<NumberType<?>, Comparable>comparing(NumberType::getValue)
                .compare(this, o);
    }
}