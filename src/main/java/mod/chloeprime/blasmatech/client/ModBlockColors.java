package mod.chloeprime.blasmatech.client;

import mod.chloeprime.blasmatech.BlasmaTech;
import mod.chloeprime.blasmatech.common.block.ModBlocks;
import mod.chloeprime.blasmatech.common.block.PottingSoilBE;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.minecraft.util.Mth.*;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBlockColors {
    public static final float DEFAULT_R = 0.45F;
    public static final float DEFAULT_G = 0.15F;
    public static final float DEFAULT_B = 0;
    public static final int DEFAULT_POTTING_SOIL_COLOR = rgb(0.5F, 0.45F, 0.15F, 0F);

    @SubscribeEvent
    public static void onBlockColors(ColorHandlerEvent.Block e) {
        e.getBlockColors().register((state, level, pos, index) -> {
            if (level == null || pos == null) {
                return DEFAULT_POTTING_SOIL_COLOR;
            }
            if (level.getBlockEntity(pos) instanceof PottingSoilBE soil) {
                var fluid = soil.getFluidTank().getFluid();
                float r, g, b;
                if (fluid.isEmpty() ||
                        fluid.getFluid() == Fluids.WATER ||
                        fluid.getFluid().defaultFluidState().is(BlasmaTech.MANA_FLUID_TAG)
                ) {
                    r = DEFAULT_R;
                    g = DEFAULT_G;
                    b = DEFAULT_B;
                } else {
                    var color = fluid.getFluid().getAttributes().getColor();
                    if ((color & 0xFFFFFF) == 0xFFFFFF) {
                        r = DEFAULT_R;
                        g = DEFAULT_G;
                        b = DEFAULT_B;
                    } else {
                        r = ((color >> 16) & 0xFF) / 255F;
                        g = ((color >> 8) & 0xFF) / 255F;
                        b = ((color) & 0xFF) / 255F;
                    }
                }
                return rgb((1 - soil.getFillRate()) / 2 + 0.5F, r, g, b);
            } else {
                return DEFAULT_POTTING_SOIL_COLOR;
            }
        }, ModBlocks.POTTING_SOIL.get());
    }

    @SubscribeEvent
    public static void onItemColors(ColorHandlerEvent.Item e) {
        e.getItemColors().register((stack, index) -> DEFAULT_POTTING_SOIL_COLOR, ModBlocks.POTTING_SOIL.get());
    }

    private static int rgb(float value, float rr, float rg, float rb) {
        var r = clamp(floor(lerp(value, rr, 1) * 255), 0, 255);
        var g = clamp(floor(lerp(value, rg, 1) * 255), 0, 255);
        var b = clamp(floor(lerp(value, rb, 1) * 255), 0, 255);
        return 0xFF000000 | (r << 16) | (g << 8) | (b);
    }
}
