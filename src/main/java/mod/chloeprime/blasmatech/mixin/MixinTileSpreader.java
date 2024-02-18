package mod.chloeprime.blasmatech.mixin;

import mod.chloeprime.blasmatech.BlasmaTech;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.botania.common.block.tile.TileExposedSimpleInventory;
import vazkii.botania.common.block.tile.mana.TileSpreader;

import java.util.Optional;

@Mixin(value = TileSpreader.class, remap = false)
public abstract class MixinTileSpreader extends TileExposedSimpleInventory {

    @SuppressWarnings("deprecation")
    @Inject(method = "commonTick", at = @At(value = "INVOKE", target = "Lvazkii/botania/common/block/tile/mana/TileSpreader;needsNewBurstSimulation()Z"))
    private static void extractManaFromFluidTanks(Level level, BlockPos worldPosition, BlockState state, TileSpreader self, CallbackInfo ci) {
        for (Direction dir : Direction.values()) {
            var relPos = worldPosition.relative(dir);
            if (!level.hasChunkAt(relPos)) {
                continue;
            }

            Optional.ofNullable(level.getBlockEntity(relPos))
                    .flatMap(te -> te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir.getOpposite()).resolve())
                    .ifPresent(pool -> {
                        int manaMissing = self.getMaxMana() - self.getCurrentMana();
                        for (Fluid manaFluid : BlasmaTech.ALL_MANA_FLUIDS.get()) {
                            int manaGot = pool.drain(new FluidStack(manaFluid, manaMissing), IFluidHandler.FluidAction.EXECUTE).getAmount();
                            self.receiveMana(manaGot);
                            manaMissing -= manaGot;
                            if (manaMissing <= 0) {
                                break;
                            }
                        }
                    });
        }
    }

    public MixinTileSpreader(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
}
