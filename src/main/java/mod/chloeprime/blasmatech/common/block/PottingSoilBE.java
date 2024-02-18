package mod.chloeprime.blasmatech.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class PottingSoilBE extends BlockEntity {
    public static final int CAPACITY = 1000;
    public PottingSoilBE(BlockPos pos, BlockState state) {
        super(ModBlocks.POTTING_SOIL_BE.get(), pos, state);
    }

    public FluidTank getFluidTank() {
        return fluidTank;
    }

    public float getFillRate() {
        return fluidTank.getFluidAmount() / (float)CAPACITY;
    }

    private final FluidTank fluidTank = new Tank(1000);
    private final LazyOptional<FluidTank> fluidTankCap = LazyOptional.of(() -> fluidTank);

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
        loadFluidTank(pkt.getTag());
        Optional.ofNullable(level).ifPresent(lvl -> lvl.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3));
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag() {
        return saveFluidTank(super.getUpdateTag());
    }

    @Override
    public void load(@Nonnull CompoundTag tag) {
        super.load(tag);
        loadFluidTank(tag);
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag tag) {
        super.saveAdditional(tag);
        saveFluidTank(tag);
    }

    private void loadFluidTank(@Nullable CompoundTag parentTag) {
        if (parentTag != null && parentTag.contains("Tank")) {
            fluidTank.readFromNBT(parentTag.getCompound("Tank"));
        }
    }

    private CompoundTag saveFluidTank(@Nonnull CompoundTag parentTag) {
        parentTag.put("Tank", fluidTank.writeToNBT(new CompoundTag()));
        return parentTag;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return fluidTankCap.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        fluidTankCap.invalidate();
        super.invalidateCaps();
    }

    public class Tank extends FluidTank {
        private boolean writing;

        public Tank(int capacity) {
            super(capacity);
        }

        @Override
        public CompoundTag writeToNBT(CompoundTag nbt) {
            try {
                writing = true;
                return super.writeToNBT(nbt);
            } finally {
                writing = false;
            }
        }

        @Override
        public void setFluid(FluidStack stack) {
            var dirty = !writing && !stack.isFluidEqual(this.fluid);
            super.setFluid(stack);
            if (dirty) {
                sync();
            }
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            var filled = super.fill(resource, action);
            if (filled > 0) {
                sync();
            }
            return filled;
        }

        @Nonnull
        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            var drained = super.drain(resource, action);
            if (drained.getAmount() > 0) {
                sync();
            }
            return drained;
        }

        @Nonnull
        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            var drained = super.drain(maxDrain, action);
            if (drained.getAmount() > 0) {
                sync();
            }
            return drained;
        }

        private void sync() {
            setChanged();
            Optional.ofNullable(level).ifPresent(lvl -> lvl.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3));
        }
    }
}
