package mod.chloeprime.blasmatech.common.particle;

import mod.chloeprime.blasmatech.BlasmaTech;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Locale;
import java.util.function.Supplier;

public enum ModParticleTypes {
    FLUID_DUST(FluidParticleData::new);

    private final ParticleEntry<?> entry;

    <D extends ParticleOptions> ModParticleTypes(Supplier<? extends ICustomParticleData<D>> typeFactory) {
        String name = name().toLowerCase(Locale.ROOT);
        entry = new ParticleEntry<>(name, typeFactory);
    }

    public static void register(IEventBus modbus) {
        ParticleEntry.REGISTER.register(modbus);
    }

    /**
     * call in {@link ParticleFactoryRegisterEvent}
     */
    @OnlyIn(Dist.CLIENT)
    public static void registerFactories() {
        ParticleEngine particles = Minecraft.getInstance().particleEngine;
        for (var particle : values())
            particle.entry.registerFactory(particles);
    }

    public ParticleType<?> get() {
        return entry.object.get();
    }

    public String parameter() {
        return entry.name;
    }

    private static class ParticleEntry<D extends ParticleOptions> {
        private static final DeferredRegister<ParticleType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, BlasmaTech.MOD_ID);

        private final String name;
        private final Supplier<? extends ICustomParticleData<D>> typeFactory;
        private final RegistryObject<ParticleType<D>> object;

        public ParticleEntry(String name, Supplier<? extends ICustomParticleData<D>> typeFactory) {
            this.name = name;
            this.typeFactory = typeFactory;

            object = REGISTER.register(name, () -> this.typeFactory.get().createType());
        }

        @OnlyIn(Dist.CLIENT)
        public void registerFactory(ParticleEngine particles) {
            typeFactory.get().register(object.get(), particles);
        }
    }
}
