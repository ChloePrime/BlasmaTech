package mod.chloeprime.blasmatech.client;

import mod.chloeprime.blasmatech.client.particle.FluidStackParticle;
import mod.chloeprime.blasmatech.common.block.PottingSoilBE;
import mod.chloeprime.blasmatech.mixin.client.ParticleEngineAccessor;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.IBlockRenderProperties;
import net.minecraftforge.fluids.FluidStack;

import java.util.Optional;

public enum PottingSoilRenderProperties implements IBlockRenderProperties {
    INSTANCE;

    private boolean isAdding;

    @Override
    public boolean addDestroyEffects(BlockState state, Level level, BlockPos pos, ParticleEngine manager) {
        if (!isAdding) {
            try {
                isAdding = true;
                getFluidFromSoilInWorld(level, pos, 0.5F).ifPresent(lb -> replicateDestroy(manager, pos, state, lb));
            } finally {
                isAdding = false;
            }
        }
        return IBlockRenderProperties.super.addDestroyEffects(state, level, pos, manager);
    }

    @Override
    public boolean addHitEffects(BlockState state, Level level, HitResult target, ParticleEngine engine) {
        if (!isAdding && target instanceof BlockHitResult hit) {
            try {
                isAdding = true;
                var pos = hit.getBlockPos();
                return getFluidFromSoilInWorld(level, pos, level.getRandom().nextFloat())
                        .map(stack -> replicateCrack(engine, state, stack, pos, hit.getDirection()))
                        .isPresent();
            } finally {
                isAdding = false;
            }
        }
        return IBlockRenderProperties.super.addHitEffects(state, level, target, engine);
    }

    private static Optional<FluidStack> getFluidFromSoilInWorld(Level level, BlockPos pos, float minFillRate) {
        return Optional.ofNullable(level.getBlockEntity(pos))
                        .flatMap(be -> Optional.ofNullable(be instanceof PottingSoilBE soil ? soil : null))
                        .filter(soil -> soil.getFillRate() >= minFillRate)
                        .map(soil -> soil.getFluidTank().getFluid());
    }

    public void replicateDestroy(ParticleEngine engine, BlockPos pPos, BlockState soil, FluidStack fluid) {
        if (soil.isAir()) {
            return;
        }
        VoxelShape voxelshape = soil.getShape(((ParticleEngineAccessor) engine).getLevel(), pPos);
        double d0 = 0.25;
        voxelshape.forAllBoxes((p_172273_, p_172274_, p_172275_, p_172276_, p_172277_, p_172278_) -> {
            double d1 = Math.min(1, p_172276_ - p_172273_);
            double d2 = Math.min(1, p_172277_ - p_172274_);
            double d3 = Math.min(1, p_172278_ - p_172275_);
            int i = Math.max(2, Mth.ceil(d1 / d0));
            int j = Math.max(2, Mth.ceil(d2 / d0));
            int k = Math.max(2, Mth.ceil(d3 / d0));

            for (int l = 0; l < i; ++l) {
                for (int i1 = 0; i1 < j; ++i1) {
                    for (int j1 = 0; j1 < k; ++j1) {
                        double d4 = ((double) l + 0.5D) / (double) i;
                        double d5 = ((double) i1 + 0.5D) / (double) j;
                        double d6 = ((double) j1 + 0.5D) / (double) k;
                        double d7 = d4 * d1 + p_172273_;
                        double d8 = d5 * d2 + p_172274_;
                        double d9 = d6 * d3 + p_172275_;
                        engine.add(new FluidStackParticle(((ParticleEngineAccessor) engine).getLevel(),  fluid, pPos.getX() + d7, pPos.getY() + d8, pPos.getZ() + d9, d4 - 0.5, d5 - 0.5, d6 - 0.5));
                    }
                }
            }
        });
    }

    private static Object replicateCrack(ParticleEngine engine, BlockState soil, FluidStack fluid, BlockPos pos, Direction side) {
        if (soil.getRenderShape() != RenderShape.INVISIBLE) {
            int i = pos.getX();
            int j = pos.getY();
            int k = pos.getZ();
            float f = 0.1F;
            AABB aabb = soil.getShape(((ParticleEngineAccessor) engine).getLevel(), pos).bounds();
            double d0 = i + ((ParticleEngineAccessor) engine).getRandom().nextDouble() * (aabb.maxX - aabb.minX - 2 * f) + f + aabb.minX;
            double d1 = j + ((ParticleEngineAccessor) engine).getRandom().nextDouble() * (aabb.maxY - aabb.minY - 2 * f) + f + aabb.minY;
            double d2 = k + ((ParticleEngineAccessor) engine).getRandom().nextDouble() * (aabb.maxZ - aabb.minZ - 2 * f) + f + aabb.minZ;
            if (side == Direction.DOWN) {
                d1 = j + aabb.minY - f;
            }

            if (side == Direction.UP) {
                d1 = j + aabb.maxY + f;
            }

            if (side == Direction.NORTH) {
                d2 = k + aabb.minZ - f;
            }

            if (side == Direction.SOUTH) {
                d2 = k + aabb.maxZ + f;
            }

            if (side == Direction.WEST) {
                d0 = i + aabb.minX - f;
            }

            if (side == Direction.EAST) {
                d0 = i + aabb.maxX + f;
            }

            engine.add((new FluidStackParticle(((ParticleEngineAccessor) engine).getLevel(), fluid, d0, d1, d2, 0, 0, 0).setPower(0.2F).scale(0.6F)));
        }

        // feed Optional.map to use isPresent() later
        return Object.class;
    }
}
