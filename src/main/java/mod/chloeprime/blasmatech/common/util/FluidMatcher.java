package mod.chloeprime.blasmatech.common.util;

import mod.chloeprime.blasmatech.BlasmaTech;
import mod.chloeprime.blasmatech.common.fluid.ModFluids;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.function.Supplier;

public class FluidMatcher {
    public static Fluid findSuitableManaFluid(IFluidHandler handler) {
        return findSuitableFluid(handler, BlasmaTech.MANA_FLUID_TAG, ModFluids.MANA_PLASMA.still());
    }

    @SuppressWarnings("deprecation")
    public static Fluid findSuitableFluid(IFluidHandler handler, TagKey<Fluid> fluidTag, Supplier<? extends Fluid> fallback) {
        var tanks = handler.getTanks();
        for (int i = 0; i < tanks; i++) {
            var content = handler.getFluidInTank(i);
            if (content.getFluid().is(fluidTag)) {
                return content.getFluid();
            }
        }
        return fallback.get();
    }
}
