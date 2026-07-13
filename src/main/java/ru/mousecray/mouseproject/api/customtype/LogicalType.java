package ru.mousecray.mouseproject.api.customtype;

import ru.mousecray.mouseproject.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mouseproject.api.error.UnsupportedValException;
import ru.mousecray.mouseproject.api.utils.MouseLogic;
import ru.mousecray.mouseproject.api.utils.MouseUtils;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Comparator;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public abstract class LogicalType<T extends Comparable<T>> extends CustomType<LogicalType<?>> {
    private final T yesVal;
    private final T noVal;
    private final T value;

    protected LogicalType(T yesVal, T noVal, boolean isYes) {
        super(CustomValType.LOGICAL);
        this.yesVal = yesVal;
        this.noVal = noVal;
        value = isYes ? yesVal : noVal;
    }

    protected T getYesVal()                         { return yesVal; }
    protected T getNoVal()                          { return noVal; }
    protected T getValue()                          { return value; }
    public boolean isTrue()                         { return value.equals(yesVal); }
    public boolean isFalse()                        { return !isTrue(); }
    public boolean asBoolean()                      { return isTrue(); }
    public int asInt()                              { return isTrue() ? 1 : 0; }

    @Override public ListType<?, ?> asListType()    { throw new UnsupportedOperationException(); }
    @Override public LogicalType<?> asLogicalType() { return this; }
    @Override public OtherType<?> asOtherType()     { throw new UnsupportedOperationException(); }

    public abstract <TYPE extends LogicalType<?>> TYPE createType(boolean isYes);

    @Override
    protected CustomCast<LogicalType<?>> createCastPipeline() {
        return new CustomCast<LogicalType<?>>() {
            @Override
            public LogicalType<T> fromValue(CustomType<?> other) {
                if (other instanceof LogicalType) {
                    LogicalType<?> type = (LogicalType<?>) other;
                    return createType(type.isTrue());
                }
                throw new UnsupportedValException();
            }

            @SuppressWarnings("unchecked") @Override
            public <TYPE extends CustomType<TYPE>> TYPE asValue(TYPE other) {
                if (other instanceof LogicalType) {
                    return (TYPE) ((LogicalType<?>) other).createType(isTrue());
                }
                throw new UnsupportedValException();
            }
        };
    }

    @Override
    protected CustomLogic<LogicalType<?>> createLogicPipeline() {
        return new CustomLogic<LogicalType<?>>() {
            @Override
            public boolean isLess(CustomType<?> other) {
                if (other instanceof LogicalType) {
                    return MouseLogic.isLess(asBoolean(), ((LogicalType<?>) other).asBoolean());
                }
                throw new UnsupportedValException();
            }

            @Override
            public boolean isMore(CustomType<?> other) {
                if (other instanceof LogicalType) {
                    return MouseLogic.isMore(asBoolean(), ((LogicalType<?>) other).asBoolean());
                }
                throw new UnsupportedValException();
            }

            @Override
            public boolean isEqual(CustomType<?> other) {
                if (other instanceof LogicalType) {
                    return MouseLogic.isEqual(asBoolean(), ((LogicalType<?>) other).asBoolean());
                }
                throw new UnsupportedValException();
            }

            @Override
            public boolean isLessOrEqual(CustomType<?> other) {
                if (other instanceof LogicalType) {
                    return MouseLogic.isLessOrEqual(asBoolean(), ((LogicalType<?>) other).asBoolean());
                }
                throw new UnsupportedValException();
            }

            @Override
            public boolean isMoreOrEqual(CustomType<?> other) {
                if (other instanceof LogicalType) {
                    return MouseLogic.isMoreOrEqual(asBoolean(), ((LogicalType<?>) other).asBoolean());
                }
                throw new UnsupportedValException();
            }

            @Override
            public LogicalType<T> not() {
                return createType(MouseLogic.invert(asBoolean()));
            }

            @Override
            public LogicalType<T> and(CustomType<?> other) {
                if (other instanceof LogicalType) {
                    return createType(
                            MouseLogic.and(asBoolean(), ((LogicalType<?>) other).asBoolean())
                    );
                }
                throw new UnsupportedValException();
            }

            @Override
            public LogicalType<T> or(CustomType<?> other) {
                if (other instanceof LogicalType) {
                    return createType(
                            MouseLogic.or(asBoolean(), ((LogicalType<?>) other).asBoolean())
                    );
                }
                throw new UnsupportedValException();
            }
        };
    }

    @Override
    protected CustomArithmetic<LogicalType<?>> createArithmeticPipeline() {
        return new CustomArithmetic<LogicalType<?>>() {
            @SuppressWarnings("unchecked") @Override
            public LogicalType<T> invert() { return (LogicalType<T>) getLogicPipeline().not(); }

            @SuppressWarnings("unchecked") @Override
            public LogicalType<T> increment() { return (LogicalType<T>) getLogicPipeline().not(); }

            @SuppressWarnings("unchecked") @Override
            public LogicalType<T> decrement() { return (LogicalType<T>) getLogicPipeline().not(); }

            @Override
            public LogicalType<T> plus(CustomType<?> other) {
                if (other instanceof LogicalType) {
                    return createType(
                            MouseLogic.plus(asBoolean(), ((LogicalType<?>) other).asBoolean())
                    );
                }
                throw new UnsupportedValException();
            }

            @Override
            public LogicalType<T> minus(CustomType<?> other) {
                if (other instanceof LogicalType) {
                    return createType(
                            MouseLogic.minus(asBoolean(), ((LogicalType<?>) other).asBoolean())
                    );
                }
                throw new UnsupportedValException();
            }

            @Override
            public LogicalType<T> divide(CustomType<?> other) {
                if (other instanceof LogicalType) {
                    return createType(
                            MouseLogic.divide(asBoolean(), ((LogicalType<?>) other).asBoolean())
                    );
                }
                throw new UnsupportedValException();
            }

            @Override
            public LogicalType<T> multiply(CustomType<?> other) {
                if (other instanceof LogicalType) {
                    return createType(
                            MouseLogic.multiply(asBoolean(), ((LogicalType<?>) other).asBoolean())
                    );
                }
                throw new UnsupportedValException();
            }

            @Override
            public LogicalType<T> modulo(CustomType<?> other) {
                if (other instanceof LogicalType) {
                    return createType(
                            MouseLogic.modulo(asBoolean(), ((LogicalType<?>) other).asBoolean())
                    );
                }
                throw new UnsupportedValException();
            }
        };
    }

    @Override public int hashCode()    { return Objects.hash(yesVal, noVal, value); }
    @Override public String toString() { return isTrue() ? yesVal.toString() : noVal.toString(); }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") @Override
    public boolean equals(Object o) {
        return MouseUtils.Equator
                .<LogicalType<T>, T>equaling(LogicalType::getValue)
                .thenEqualing(LogicalType::getYesVal)
                .thenEqualing(LogicalType::getNoVal)
                .equals(getClass(), this, o);
    }

    @Override
    public int compareTo(@Nonnull LogicalType<?> o) {
        return Comparator
                .<LogicalType<?>, Comparable>comparing(LogicalType::getValue)
                .thenComparing(LogicalType::getYesVal)
                .thenComparing(LogicalType::getNoVal)
                .compare(this, o);
    }
}