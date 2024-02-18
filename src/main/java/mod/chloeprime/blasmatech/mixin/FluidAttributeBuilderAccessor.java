package mod.chloeprime.blasmatech.mixin;

import net.minecraftforge.fluids.FluidAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = FluidAttributes.Builder.class, remap = false)
public interface FluidAttributeBuilderAccessor {
    @Accessor int getLuminosity();
}
