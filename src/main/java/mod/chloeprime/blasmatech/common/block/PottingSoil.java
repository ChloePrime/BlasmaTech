package mod.chloeprime.blasmatech.common.block;

import mod.chloeprime.blasmatech.client.PottingSoilRenderProperties;
import mod.chloeprime.blasmatech.common.particle.FluidParticleData;
import mod.chloeprime.blasmatech.common.particle.ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.IBlockRenderProperties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
public class PottingSoil extends Block implements EntityBlock {
    public PottingSoil(Properties props) {
        super(props);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return new PottingSoilBE(pos, state);
    }

    @Override
    protected void spawnDestroyParticles(Level pLevel, Player pPlayer, BlockPos pPos, BlockState pState) {
        super.spawnDestroyParticles(pLevel, pPlayer, pPos, pState);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, Random rng) {
        super.animateTick(state, level, pos, rng);
        Optional.ofNullable(level.getBlockEntity(pos))
                .flatMap(be -> Optional.ofNullable(be instanceof PottingSoilBE tank ? tank : null))
                .filter(be -> !be.getFluidTank().getFluid().isEmpty())
                .ifPresent(tank -> {
                    if (rng.nextFloat() > tank.getFillRate()) {
                        return;
                    }
                    var flags = rng.nextInt() >> 2;
                    var isY = (flags & 1) != 0;
                    var greaterXZ = (flags & 4) != 0;
                    var randomIsX = (flags & 8) != 0;
                    var randomIsZ = !randomIsX;
                    var x = pos.getX() + ((randomIsX || isY) ? rng.nextFloat() : (greaterXZ ? 1 : 0));
                    var z = pos.getZ() + ((randomIsZ || isY) ? rng.nextFloat() : (greaterXZ ? 1 : 0));
                    var y = pos.getY() + (isY ? 1.03125 : 0.5);
                    var direction = (isY ? Direction.UP : (randomIsX
                            ? (greaterXZ ? Direction.SOUTH : Direction.NORTH )
                            : (greaterXZ ? Direction.EAST : Direction.WEST))).getNormal();
                    var speed = isY ? 0.1F : 0.05F;

                    var fluid = tank.getFluidTank().getFluid();
                    level.addParticle(new FluidParticleData(
                            ModParticleTypes.FLUID_DUST.get(), fluid),
                            x, y, z,
                            direction.getX() * speed, direction.getY() * speed, direction.getZ() * speed
                    );
                });
    }

    @Override
    public void initializeClient(Consumer<IBlockRenderProperties> consumer) {
        consumer.accept(PottingSoilRenderProperties.INSTANCE);
    }
}
