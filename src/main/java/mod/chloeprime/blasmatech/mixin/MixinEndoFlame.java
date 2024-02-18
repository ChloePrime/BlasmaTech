package mod.chloeprime.blasmatech.mixin;

import mod.chloeprime.blasmatech.common.util.PipeChecker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vazkii.botania.api.subtile.TileEntitySpecialFlower;
import vazkii.botania.common.block.subtile.generating.SubTileEndoflame;

@Mixin(value = SubTileEndoflame.class, remap = false)
public abstract class MixinEndoFlame extends TileEntitySpecialFlower {
    @Redirect(method = "tickFlower", at = @At(value = "INVOKE", target = "Lvazkii/botania/common/block/subtile/generating/SubTileEndoflame;isValidBinding()Z"))
    private boolean considerPipesAsValidBind(SubTileEndoflame be) {
        return be.isValidBinding() || PipeChecker.isPlantablePipe(getLevel(), getBlockPos().below());
    }

    public MixinEndoFlame(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
}
