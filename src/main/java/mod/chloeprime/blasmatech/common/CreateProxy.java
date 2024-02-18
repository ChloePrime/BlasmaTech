package mod.chloeprime.blasmatech.common;

import com.simibubi.create.content.fluids.pipes.EncasedPipeBlock;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;

public class CreateProxy {
    public static boolean isValidCreatePipe(BlockState state, Direction direction) {
        var block = state.getBlock();
        if (block instanceof FluidPipeBlock) {
            return true;
        }
        if (block instanceof EncasedPipeBlock) {
            return state.getValue(PipeBlock.PROPERTY_BY_DIRECTION.get(direction));
        }
        // AxisPipe 不在这里处理
        return false;
    }
}
