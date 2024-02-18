package mod.chloeprime.blasmatech.common;

import net.minecraft.world.level.block.entity.BlockEntity;
import vazkii.botania.api.mana.IManaReceiver;
import vazkii.botania.api.subtile.TileEntityFunctionalFlower;
import vazkii.botania.api.subtile.TileEntityGeneratingFlower;

public interface ManaMachine {
    void receiveMana(int amount);
    int getMaxMana();
    int getCurrentMana();
    boolean canReceive();
    boolean canExtract();

    static ManaMachine of(BlockEntity be) {
        if (be instanceof IManaReceiver receiver) {
            return of(receiver);
        }
        if (be instanceof TileEntityGeneratingFlower generator) {
            return of(generator);
        }
        if (be instanceof TileEntityFunctionalFlower machine) {
            return of(machine);
        }
        throw new IllegalArgumentException("given BlockEntity is not a supported mana machine");
    }

    static ManaMachine of(IManaReceiver receiver) {
        var maxGetter = MaxManaGetter.of(receiver);
        return new ManaMachine() {
            @Override
            public void receiveMana(int amount) {
                receiver.receiveMana(amount);
            }

            @Override
            public int getMaxMana() {
                return maxGetter.getAsInt();
            }

            @Override
            public int getCurrentMana() {
                return receiver.getCurrentMana();
            }

            @Override
            public boolean canReceive() {
                return true;
            }

            @Override
            public boolean canExtract() {
                return true;
            }
        };
    }

    static ManaMachine of(TileEntityGeneratingFlower flower) {
        return new ManaMachine() {
            @Override
            public void receiveMana(int amount) {
                var before = flower.getMana();
                flower.addMana(Math.max(-flower.getMana(), amount));
                if (flower.getMana() != before) {
                    flower.sync();
                }
            }

            @Override
            public int getMaxMana() {
                return flower.getMaxMana();
            }

            @Override
            public int getCurrentMana() {
                return flower.getMana();
            }

            @Override
            public boolean canReceive() {
                return false;
            }

            @Override
            public boolean canExtract() {
                return true;
            }
        };
    }


    static ManaMachine of(TileEntityFunctionalFlower flower) {
        return new ManaMachine() {
            @Override
            public void receiveMana(int amount) {
                var before = flower.getMana();
                flower.addMana(amount);
                if (flower.getMana() != before) {
                    flower.sync();
                }
            }

            @Override
            public int getMaxMana() {
                return flower.getMaxMana();
            }

            @Override
            public int getCurrentMana() {
                return flower.getMana();
            }

            @Override
            public boolean canReceive() {
                return getMaxMana() > 0;
            }

            @Override
            public boolean canExtract() {
                return false;
            }
        };
    }
}
