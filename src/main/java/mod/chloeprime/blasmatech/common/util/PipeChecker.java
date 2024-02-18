package mod.chloeprime.blasmatech.common.util;

import mod.chloeprime.blasmatech.BlasmaTech;
import mod.chloeprime.blasmatech.common.CreateProxy;
import mod.chloeprime.blasmatech.common.compat.Compatibility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class PipeChecker {
    public static boolean isPlantablePipe(@Nullable BlockGetter level, BlockPos pos) {
        return isPipe(level, pos, Direction.UP);
    }

    public static boolean isPipe(@Nullable BlockGetter level, BlockPos pos, Direction direction) {
        return Optional.ofNullable(level)
                .flatMap(lvl -> checkPipeCapability(lvl, pos, direction).map(b -> b || checkNoCapabilityPipe(lvl, pos, direction).orElse(false)))
                .orElse(false);
    }

    private static Optional<Boolean> checkPipeCapability(@Nonnull BlockGetter level, BlockPos pos, Direction direction) {
        return Optional.ofNullable(level.getBlockEntity(pos))
                .map(te -> te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction).isPresent());
    }

    private static Optional<Boolean> checkNoCapabilityPipe(@Nonnull BlockGetter level, BlockPos pos, Direction direction) {
        var state = level.getBlockState(pos);
        if (Compatibility.HAS_CREATE && CreateProxy.isValidCreatePipe(state, direction)) {
            return Optional.of(true);
        }
        if (state.is(BlasmaTech.FLUID_PIPE_TAG)) {
            if (state.hasProperty(RotatedPillarBlock.AXIS)) {
                return Optional.of(state.getValue(RotatedPillarBlock.AXIS) == direction.getAxis());
            }
            return Optional.of(true);
        }
        return Optional.empty();
    }
}
