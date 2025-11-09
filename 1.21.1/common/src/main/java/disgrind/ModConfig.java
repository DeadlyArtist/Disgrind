package disgrind;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.config.reflection.Comment;
import net.blay09.mods.balm.api.config.reflection.Config;

@Config(value = ModEntry.MOD_ID, type = "common")
public class ModConfig {

    public static ModConfig get() {
        return Balm.getConfig().getActiveConfig(ModConfig.class);
    }

    @Comment("Number of xp levels required")
    public int levelsRequired = 1;
}
