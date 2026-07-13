/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mouseproject.core.common.capability;

import ru.mousecray.mouseproject.core.common.inventory.MPInventory;

public interface ICapabilityInventory<T extends ICapabilityInventory<T>> {
    void copyInventory(T inventory);
    MPInventory getInventory();
}