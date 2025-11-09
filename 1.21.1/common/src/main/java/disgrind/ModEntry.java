package disgrind;

import net.blay09.mods.balm.api.Balm;

public final class ModEntry {
    public static final String MOD_ID = "disgrind";

    public static void init() {
        // Write common init code here.

        Balm.getConfig().registerConfig(ModConfig.class);
    }

    public static void preInitialize() {

    }
}
