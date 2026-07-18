/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mouseproject.api.customtype;

public interface CustomLogic<T extends CustomType<?>> {
    static <TYPE extends CustomType<TYPE>> CustomLogic<TYPE> DEFAULT() {
        return new CustomLogic<TYPE>() {
            @Override public boolean isLess(CustomType<?> other)        { throw new UnsupportedOperationException(); }
            @Override public boolean isMore(CustomType<?> other)        { throw new UnsupportedOperationException(); }
            @Override public boolean isEqual(CustomType<?> other)       { throw new UnsupportedOperationException(); }
            @Override public boolean isLessOrEqual(CustomType<?> other) { throw new UnsupportedOperationException(); }
            @Override public boolean isMoreOrEqual(CustomType<?> other) { throw new UnsupportedOperationException(); }
            @Override public TYPE not()                                 { throw new UnsupportedOperationException(); }
            @Override public TYPE and(CustomType<?> other)              { throw new UnsupportedOperationException(); }
            @Override public TYPE or(CustomType<?> other)               { throw new UnsupportedOperationException(); }
        };
    }

    boolean isLess(CustomType<?> other);
    boolean isMore(CustomType<?> other);
    boolean isEqual(CustomType<?> other);
    boolean isLessOrEqual(CustomType<?> other);
    boolean isMoreOrEqual(CustomType<?> other);

    T not();

    T and(CustomType<?> other);
    T or(CustomType<?> other);
}