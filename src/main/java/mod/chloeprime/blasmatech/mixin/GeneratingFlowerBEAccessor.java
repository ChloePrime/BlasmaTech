package mod.chloeprime.blasmatech.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import vazkii.botania.api.subtile.TileEntityGeneratingFlower;

@Mixin(value = TileEntityGeneratingFlower.class, remap = false)
public interface GeneratingFlowerBEAccessor {
    @Accessor void setMana(int mana);
}
