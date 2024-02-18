package mod.chloeprime.blasmatech.common.compat;

import net.minecraftforge.fml.ModList;

public class Compatibility {
    public static boolean HAS_CREATE = ModList.get().isLoaded("create");

    private Compatibility() {}
}
