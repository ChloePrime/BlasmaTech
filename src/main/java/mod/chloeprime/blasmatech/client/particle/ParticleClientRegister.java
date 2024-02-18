package mod.chloeprime.blasmatech.client.particle;

import mod.chloeprime.blasmatech.common.particle.ModParticleTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ParticleClientRegister {
    @SubscribeEvent
    public static void onRegisterParticles(ParticleFactoryRegisterEvent e) {
        ModParticleTypes.registerFactories();
    }
}
