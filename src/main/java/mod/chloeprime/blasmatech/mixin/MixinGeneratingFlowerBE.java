package mod.chloeprime.blasmatech.mixin;

import mod.chloeprime.blasmatech.common.util.FluidMatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.botania.api.mana.IManaCollector;
import vazkii.botania.api.subtile.TileEntityBindableSpecialFlower;
import vazkii.botania.api.subtile.TileEntityGeneratingFlower;

import java.util.Optional;

@Mixin(value = TileEntityGeneratingFlower.class, remap = false)
public abstract class MixinGeneratingFlowerBE extends TileEntityBindableSpecialFlower<IManaCollector> {
    @Shadow public abstract int getMana();

    @Shadow public abstract void addMana(int mana);

    @Inject(method = "tickFlower", at = @At("TAIL"))
    private void pumpManaToPipes(CallbackInfo ci) {
        if (isFloating()) {
            return;
        }
        Optional.ofNullable(getLevel())
                .flatMap(level -> level.isClientSide ? Optional.empty() : Optional.ofNullable(level.getBlockEntity(getBlockPos().below())))
                .flatMap(be -> be.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, Direction.UP).resolve())
                .ifPresent(tank -> {
                    if (getMana() <= 0) {
                        return;
                    }
                    var manaFluid = new FluidStack(FluidMatcher.findSuitableManaFluid(tank), getMana());
                    var manaFilled = tank.fill(manaFluid, IFluidHandler.FluidAction.EXECUTE);
                    addMana(-manaFilled);
                    sync();
                });
    }

    public MixinGeneratingFlowerBE(BlockEntityType<?> type, BlockPos pos, BlockState state, Class<IManaCollector> bindClass) {
        super(type, pos, state, bindClass);
    }
}
