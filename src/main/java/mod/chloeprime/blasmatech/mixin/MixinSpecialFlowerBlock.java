package mod.chloeprime.blasmatech.mixin;

import mod.chloeprime.blasmatech.common.util.PipeChecker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.botania.common.block.BlockSpecialFlower;

@Mixin(value = BlockSpecialFlower.class, remap = false)
public class MixinSpecialFlowerBlock {
    @Inject(method = "mayPlaceOn", remap = true, at = @At("RETURN"), cancellable = true)
    private void mayPlaceOnPipes(BlockState state, BlockGetter level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            return;
        }
        cir.setReturnValue(PipeChecker.isPlantablePipe(level, pos));
    }
}
