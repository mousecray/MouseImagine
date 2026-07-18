/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mouseproject.api.customtype;

public interface CustomCast<T extends CustomType<?>> {
    static <TYPE extends CustomType<TYPE>> CustomCast<TYPE> DEFAULT() {
        return new CustomCast<TYPE>() {
            @Override public TYPE fromValue(CustomType<?> other) { throw new UnsupportedOperationException(); }
            @Override
            public <TYPE1 extends CustomType<TYPE1>> TYPE1 asValue(TYPE1 other) { throw new UnsupportedOperationException(); }
        };
    }

    T fromValue(CustomType<?> other);
    <TYPE extends CustomType<TYPE>> TYPE asValue(TYPE other);
}