package mod.chloeprime.blasmatech.mixin;

import mod.chloeprime.blasmatech.common.util.PipeChecker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.botania.common.block.BlockSpecialFlower;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class MixinBlockStateBase {
    @Shadow public abstract Block getBlock();

    @Inject(method = "getOffset", at = @At("HEAD"), cancellable = true)
    private void doNotOffsetOnSpecFlowerOnPipe(BlockGetter level, BlockPos pos, CallbackInfoReturnable<Vec3> cir) {
        if (!(getBlock() instanceof BlockSpecialFlower)) {
            return;
        }

        if (PipeChecker.isPlantablePipe(level, pos.below())) {
            cir.setReturnValue(Vec3.ZERO);
        }
    }
}
