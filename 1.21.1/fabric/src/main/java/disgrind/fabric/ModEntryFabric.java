package disgrind.fabric;

import disgrind.fabric.utils.LoaderImpl;
import disgrind.utils.Loader;
import net.fabricmc.api.ModInitializer;

import disgrind.ModEntry;

public final class ModEntryFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        ModEntry.init();
        RegistryInitManagerFabric.init();
    }

    public static void preInitialize() {
        Loader._impl = new LoaderImpl();
    }
}
