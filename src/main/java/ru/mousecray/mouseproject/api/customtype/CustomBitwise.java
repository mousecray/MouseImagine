package ru.mousecray.mouseproject.api.customtype;

public interface CustomBitwise<T extends CustomType<?>> {
    static <TYPE extends CustomType<TYPE>> CustomBitwise<TYPE> DEFAULT() {
        return new CustomBitwise<TYPE>() {
            @Override public TYPE and(CustomType<?> other) { throw new UnsupportedOperationException(); }
            @Override public TYPE or(CustomType<?> other)  { throw new UnsupportedOperationException(); }
            @Override public TYPE xor(CustomType<?> other) { throw new UnsupportedOperationException(); }
            @Override public TYPE not()                    { throw new UnsupportedOperationException(); }
            @Override public TYPE leftShift(int other)     { throw new UnsupportedOperationException(); }
            @Override public TYPE rightShift(int other)    { throw new UnsupportedOperationException(); }
            @Override public TYPE uRightShift(int other)   { throw new UnsupportedOperationException(); }
        };
    }

    T and(CustomType<?> other);
    T or(CustomType<?> other);
    T xor(CustomType<?> other);

    T not();

    T leftShift(int other);
    T rightShift(int other);
    T uRightShift(int other);
}