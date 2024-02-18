package mod.chloeprime.blasmatech.common.particle;

import com.mojang.serialization.Codec;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleOptions.Deserializer;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface ICustomParticleData<T extends ParticleOptions> {

    Deserializer<T> getDeserializer();

    Codec<T> getCodec(ParticleType<T> type);

    default ParticleType<T> createType() {
        return new ParticleType<T>(false, getDeserializer()) {

            @Override
            public Codec<T> codec() {
                return ICustomParticleData.this.getCodec(this);
            }
        };
    }

    @OnlyIn(Dist.CLIENT)
    ParticleProvider<T> getFactory();

    @OnlyIn(Dist.CLIENT)
    default void register(ParticleType<T> type, ParticleEngine particles) {
        particles.register(type, getFactory());
    }

}
