package mod.chloeprime.blasmatech.client;

import mod.chloeprime.blasmatech.common.fluid.ModFluids;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FluidRenderTypeSetup {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent e) {
        e.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(ModFluids.MANA_PLASMA.still().get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.MANA_PLASMA.flowing().get(), RenderType.translucent());
        });
    }
}
