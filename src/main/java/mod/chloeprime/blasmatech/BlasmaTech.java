package mod.chloeprime.blasmatech;

import mod.chloeprime.blasmatech.common.block.ModBlocks;
import mod.chloeprime.blasmatech.common.fluid.ModFluids;
import mod.chloeprime.blasmatech.common.particle.ModParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;
import java.util.Optional;

@Mod(BlasmaTech.MOD_ID)
public class BlasmaTech {
    public static final String MOD_ID = "blasmatech";
    public static final TagKey<Fluid> MANA_FLUID_TAG = FluidTags.create(new ResourceLocation(MOD_ID, "mana"));
    public static final TagKey<Block> FLUID_PIPE_TAG = BlockTags.create(new ResourceLocation(MOD_ID, "fluid_pipes"));
    public static final Lazy<Iterable<Fluid>> ALL_MANA_FLUIDS = Lazy.of(() -> Optional.ofNullable(ForgeRegistries.FLUIDS.tags())
            .map(tags -> (Iterable<Fluid>) tags.getTag(MANA_FLUID_TAG))
            .orElse(Collections.emptyList()));

    public BlasmaTech() {
        var modbus = FMLJavaModLoadingContext.get().getModEventBus();
        ModFluids.FLUIDS.register(modbus);
        ModFluids.FLUID_BLOCKS.register(modbus);
        ModFluids.BUCKETS.register(modbus);

        ModBlocks.BLOCKS.register(modbus);
        ModBlocks.BLOCK_ENTITIES.register(modbus);
        ModBlocks.BLOCK_ITEMS.register(modbus);

        ModParticleTypes.register(modbus);
    }

    public static ResourceLocation loc(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
