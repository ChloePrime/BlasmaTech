package mod.chloeprime.blasmatech.mixin.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Random;

@Mixin(ParticleEngine.class)
public interface ParticleEngineAccessor {
    @Accessor Random getRandom();
    @Accessor ClientLevel getLevel();
}
