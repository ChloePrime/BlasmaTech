package mod.chloeprime.blasmatech.common.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mod.chloeprime.blasmatech.client.particle.FluidStackParticle;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

public class FluidParticleData implements ParticleOptions, ICustomParticleData<FluidParticleData> {

    private ParticleType<FluidParticleData> type;
    private FluidStack fluid;

    public FluidParticleData() {}

    @SuppressWarnings("unchecked")
    public FluidParticleData(ParticleType<?> type, FluidStack fluid) {
        this.type = (ParticleType<FluidParticleData>) type;
        this.fluid = fluid;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ParticleProvider<FluidParticleData> getFactory() {
        return (data, world, x, y, z, vx, vy, vz) -> FluidStackParticle.create(data.type, world, data.fluid, x, y, z,
                vx, vy, vz);
    }

    @Nonnull
    @Override
    public ParticleType<?> getType() {
        return type;
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeFluidStack(fluid);
    }

    @Nonnull
    @Override
    public String writeToString() {
        var typeName = Objects.requireNonNull(type.getRegistryName());
        var fluidName = Objects.requireNonNull(fluid.getFluid().getRegistryName());
        return typeName + " " + fluidName;
    }

    public static final Codec<FluidParticleData> CODEC = RecordCodecBuilder.create(i -> i
            .group(FluidStack.CODEC.fieldOf("fluid")
                    .forGetter(p -> p.fluid))
            .apply(i, fs -> new FluidParticleData(ModParticleTypes.FLUID_DUST.get(), fs)));

    @ParametersAreNonnullByDefault
    @MethodsReturnNonnullByDefault
    @SuppressWarnings("deprecation")
    public static final ParticleOptions.Deserializer<FluidParticleData> DESERIALIZER =
            new ParticleOptions.Deserializer<>() {
                public FluidParticleData fromCommand(ParticleType<FluidParticleData> particleTypeIn, StringReader reader) {
                    return new FluidParticleData(particleTypeIn, new FluidStack(Fluids.WATER, 1));
                }

                public FluidParticleData fromNetwork(ParticleType<FluidParticleData> particleTypeIn, FriendlyByteBuf buffer) {
                    return new FluidParticleData(particleTypeIn, buffer.readFluidStack());
                }
            };

    @Override
    @SuppressWarnings("deprecation")
    public Deserializer<FluidParticleData> getDeserializer() {
        return DESERIALIZER;
    }

    @Override
    public Codec<FluidParticleData> getCodec(ParticleType<FluidParticleData> type) {
        return CODEC;
    }

}