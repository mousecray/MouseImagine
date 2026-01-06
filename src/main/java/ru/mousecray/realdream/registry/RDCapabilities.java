package ru.mousecray.realdream.registry;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import ru.mousecray.realdream.common.capability.CapabilityInventoryStorage;
import ru.mousecray.realdream.common.capability.ICapabilityInventory;
import ru.mousecray.realdream.common.capability.ICapabilityInventoryProvider;
import ru.mousecray.realdream.common.capability.impl.CapabilityWalletInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class RDCapabilities {
    public static final RDCapabilities INSTANCE = new RDCapabilities();

    private final List<ICapabilityInventoryProvider<?>> invToSave;

    private RDCapabilities() {
        invToSave = new ArrayList<>();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void register() {
        registerInvToSave(new CapabilityInventoryStorage(), new CapabilityWalletInventory.InventoryProvider(), CapabilityWalletInventory::new);
    }

    @SuppressWarnings("rawtypes")
    private void registerInvToSave(Capability.IStorage<ICapabilityInventory> storage, ICapabilityInventoryProvider<?> provider, Callable<ICapabilityInventory> factory) {
        CapabilityManager.INSTANCE.register(ICapabilityInventory.class, storage, factory);
        invToSave.add(provider);
    }

    public List<ICapabilityInventoryProvider<?>> getInvsToSave() { return invToSave; }
}