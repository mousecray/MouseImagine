package ru.mousecray.mouseproject.api.customtype;

import ru.mousecray.mouseproject.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mouseproject.api.error.UnsupportedValException;
import ru.mousecray.mouseproject.api.utils.MouseUtils;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Comparator;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public abstract class OtherType<T extends Comparable<T>> extends CustomType<OtherType<?>> {
    protected final T value;

    protected OtherType(T value) {
        super(CustomValType.OTHER);
        this.value = value;
    }

    public T getValue() { return value; }
    public abstract <TYPE extends OtherType<T>> TYPE createType(Object value);

    @Override
    protected CustomCast<OtherType<?>> createCastPipeline() {
        return new CustomCast<OtherType<?>>() {
            @Override
            public OtherType<T> fromValue(CustomType<?> other) {
                if (other instanceof OtherType) {
                    OtherType<?> type = (OtherType<?>) other;
                    return createType(type.getValue());
                }
                throw new UnsupportedValException();
            }

            @Override
            public <TYPE extends CustomType<TYPE>> TYPE asValue(TYPE other) {
                if (other instanceof OtherType) {
                    return (TYPE) ((OtherType<?>) other).createType(getValue());
                }
                throw new UnsupportedValException();
            }
        };
    }

    @Override
    protected CustomLogic<OtherType<?>> createLogicPipeline() {
        return new CustomLogic<OtherType<?>>() {
            @Override public boolean isLess(CustomType<?> other)        { throw new UnsupportedOperationException(); }
            @Override public boolean isMore(CustomType<?> other)        { throw new UnsupportedOperationException(); }
            @Override public boolean isLessOrEqual(CustomType<?> other) { throw new UnsupportedOperationException(); }
            @Override public boolean isMoreOrEqual(CustomType<?> other) { throw new UnsupportedOperationException(); }
            @Override public OtherType<?> not()                         { throw new UnsupportedOperationException(); }
            @Override public OtherType<?> and(CustomType<?> other)      { throw new UnsupportedOperationException(); }
            @Override public OtherType<?> or(CustomType<?> other)       { throw new UnsupportedOperationException(); }

            @Override
            public boolean isEqual(CustomType<?> other) {
                if (other instanceof OtherType) return value.equals(((OtherType<?>) other).value);
                throw new UnsupportedValException();
            }
        };
    }

    @Override public ListType<?, ?> asListType()    { throw new UnsupportedOperationException(); }
    @Override public LogicalType<?> asLogicalType() { throw new UnsupportedOperationException(); }
    @Override public NumberType<?> asNumberType()   { throw new UnsupportedOperationException(); }
    @Override public OtherType<?> asOtherType()     { return this; }

    @Override public int hashCode()                 { return Objects.hash(value); }
    @Override public String toString()              { return value.toString(); }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") @Override
    public boolean equals(Object o) {
        return MouseUtils.Equator
                .<OtherType<T>, T>equaling(OtherType::getValue)
                .equals(getClass(), this, o);
    }

    @Override
    public int compareTo(@Nonnull OtherType<?> o) {
        return Comparator
                .<OtherType<?>, Comparable>comparing(OtherType::getValue)
                .compare(this, o);
    }
}