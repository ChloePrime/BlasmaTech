package mod.chloeprime.blasmatech.common;

import vazkii.botania.api.mana.IManaCollector;
import vazkii.botania.api.mana.IManaReceiver;
import vazkii.botania.common.block.tile.mana.TilePool;

import java.lang.ref.WeakReference;
import java.util.Optional;
import java.util.function.IntSupplier;

public abstract class MaxManaGetter implements IntSupplier {

    public static MaxManaGetter of(IManaReceiver machine) {
        if (machine instanceof IManaCollector collector) {
            return new ApiDelegate(collector::getMaxMana);
        }
        if (machine instanceof TilePool pool) {
            return new ApiDelegate(() -> pool.manaCap);
        }

        return new TestBasedAdapter(machine);
    }

    public static class ApiDelegate extends MaxManaGetter {
        public ApiDelegate(IntSupplier delegate) {
            this.delegate = delegate;
        }

        private final IntSupplier delegate;

        @Override
        public int getAsInt() {
            return delegate.getAsInt();
        }
    }

    public static class TestBasedAdapter extends MaxManaGetter {
        public TestBasedAdapter(IManaReceiver machine) {
            this.machine = new WeakReference<>(machine);
        }

        @Override
        public int getAsInt() {
            if (machine != null) {
                Optional.ofNullable(machine.get())
                        .ifPresent(mach -> {
                            var before = mach.getCurrentMana();
                            mach.receiveMana(Integer.MAX_VALUE);
                            value = mach.getCurrentMana();
                            mach.receiveMana(before - value);
                        });
                machine = null;
            }
            return value;
        }

        private WeakReference<IManaReceiver> machine;
        private int value = Integer.MAX_VALUE;
    }
}
