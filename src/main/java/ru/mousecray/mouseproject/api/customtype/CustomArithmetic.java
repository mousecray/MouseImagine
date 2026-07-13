package ru.mousecray.mouseproject.api.customtype;

public interface CustomArithmetic<T extends CustomType<?>> {
    static <TYPE extends CustomType<TYPE>> CustomArithmetic<TYPE> DEFAULT() {
        return new CustomArithmetic<TYPE>() {
            @Override public TYPE invert()                      { throw new UnsupportedOperationException(); }
            @Override public TYPE increment()                   { throw new UnsupportedOperationException(); }
            @Override public TYPE decrement()                   { throw new UnsupportedOperationException(); }
            @Override public TYPE plus(CustomType<?> other)     { throw new UnsupportedOperationException(); }
            @Override public TYPE minus(CustomType<?> other)    { throw new UnsupportedOperationException(); }
            @Override public TYPE divide(CustomType<?> other)   { throw new UnsupportedOperationException(); }
            @Override public TYPE multiply(CustomType<?> other) { throw new UnsupportedOperationException(); }
            @Override public TYPE modulo(CustomType<?> other)   { throw new UnsupportedOperationException(); }
        };
    }

    T invert();
    T increment();
    T decrement();

    T plus(CustomType<?> other);
    T minus(CustomType<?> other);
    T divide(CustomType<?> other);
    T multiply(CustomType<?> other);
    T modulo(CustomType<?> other);
}
