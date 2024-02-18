package mod.chloeprime.blasmatech.common;

import mod.chloeprime.blasmatech.BlasmaTech;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.mana.IManaPool;
import vazkii.botania.api.mana.IManaReceiver;
import vazkii.botania.api.mana.IManaSpreader;
import vazkii.botania.api.subtile.TileEntityFunctionalFlower;
import vazkii.botania.api.subtile.TileEntityGeneratingFlower;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.Optional;

import static mod.chloeprime.blasmatech.BlasmaTech.MANA_FLUID_TAG;

public class ManaTileFluidCapability implements IFluidHandler {

    public ManaTileFluidCapability(ManaMachine machine) {
        this.machine = machine;
    }

    public final ManaMachine machine;

    @Override
    public int getTanks() {
        return 1;
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {
        if (tank != 0) {
            return FluidStack.EMPTY;
        }
        return findManaFluid()
                .map(fluid -> new FluidStack(fluid, machine.getCurrentMana()))
                .orElse(FluidStack.EMPTY);
    }

    @Override
    public int getTankCapacity(int tank) {
        return tank == 0 ? machine.getMaxMana() : 0;
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return tank == 0 && stack.getFluid().defaultFluidState().is(MANA_FLUID_TAG);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (!machine.canReceive()) {
            return 0;
        }
        if (!resource.getFluid().defaultFluidState().is(MANA_FLUID_TAG)) {
            return 0;
        }
        int manaBefore = machine.getCurrentMana();
        int amount = Math.min(resource.getAmount(), machine.getMaxMana() - manaBefore);
        if (action.execute()) {
            machine.receiveMana(amount);
        }
        return amount;
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (!machine.canExtract()) {
            return FluidStack.EMPTY;
        }
        if (!resource.getFluid().defaultFluidState().is(MANA_FLUID_TAG)) {
            return FluidStack.EMPTY;
        }
        int manaBefore = machine.getCurrentMana();
        int amount = Math.min(resource.getAmount(), manaBefore);
        var drained = resource.copy();
        if (action.execute()) {
            machine.receiveMana(-amount);
        }
        drained.setAmount(amount);
        return drained;
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        return findManaFluid()
                .map(f -> new FluidStack(f, maxDrain))
                .map(fs -> this.drain(fs, action))
                .orElse(FluidStack.EMPTY);
    }

    private static Optional<Fluid> findManaFluid() {
        return Optional.of(MANA_FLUID.get());
    }

    private static final Lazy<Fluid> MANA_FLUID = Lazy.of(
            () -> Optional.ofNullable(ForgeRegistries.FLUIDS.tags())
                    .flatMap(m -> m.getTag(MANA_FLUID_TAG).stream().findAny())
                    .orElse(null)
    );

    public static class Provider implements ICapabilityProvider {
        public Provider(IManaReceiver receiver, EnumSet<Direction> directions) {
            this.instance = LazyOptional.of(() -> new ManaTileFluidCapability(ManaMachine.of(receiver)));
            this.directions = directions;
        }

        public Provider(TileEntityGeneratingFlower receiver, EnumSet<Direction> directions) {
            this.instance = LazyOptional.of(() -> new ManaTileFluidCapability(ManaMachine.of(receiver)));
            this.directions = directions;
        }


        public Provider(TileEntityFunctionalFlower machine, EnumSet<Direction> directions) {
            this.instance = LazyOptional.of(() -> new ManaTileFluidCapability(ManaMachine.of(machine)));
            this.directions = directions;
        }

        private final LazyOptional<IFluidHandler> instance;
        private final EnumSet<Direction> directions;

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            if (directions != null && !directions.contains(side)) {
                return LazyOptional.empty();
            }
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.orEmpty(cap, instance);
        }
    }

    @Mod.EventBusSubscriber
    public static class Attacher {
        @SubscribeEvent
        static void onAttachCap(AttachCapabilitiesEvent<BlockEntity> e) {
            if (e.getObject() instanceof IManaSpreader spreader) {
                e.addCapability(CAP_KEY, new Provider(spreader, null));
                return;
            }
            if (e.getObject() instanceof IManaPool pool) {
                e.addCapability(CAP_KEY, new Provider(pool, POOL_VALID_DIRECTIONS));
                return;
            }
            if (e.getObject() instanceof TileEntityGeneratingFlower generator) {
                e.addCapability(CAP_KEY, new Provider(generator, FLOWER_VALID_DIRECTIONS));
                return;
            }
            if (e.getObject() instanceof TileEntityFunctionalFlower machine) {
                e.addCapability(CAP_KEY, new Provider(machine, FLOWER_VALID_DIRECTIONS));
            }
        }

        public static final EnumSet<Direction> POOL_VALID_DIRECTIONS = EnumSet.of(Direction.DOWN);
        public static final EnumSet<Direction> FLOWER_VALID_DIRECTIONS = POOL_VALID_DIRECTIONS;
        public static final ResourceLocation CAP_KEY = BlasmaTech.loc("mana_tank");
    }
}
