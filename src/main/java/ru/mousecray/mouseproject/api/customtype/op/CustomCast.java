/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mouseproject.api.customtype.op;

import ru.mousecray.mouseproject.api.customtype.CustomType;

public interface CustomCast<T extends CustomType<?>> {
    <TYPE extends CustomType<?>> TYPE asValue(Class<TYPE> targetClass);
}