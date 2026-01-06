package ru.mousecray.realdream.common.capability;

import ru.mousecray.realdream.common.inventory.RDInventory;

public interface ICapabilityInventory<T extends ICapabilityInventory<T>> {
    void copyInventory(T inventory);
    RDInventory getInventory();
}