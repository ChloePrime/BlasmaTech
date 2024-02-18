package mod.chloeprime.blasmatech.common.block;

import mod.chloeprime.blasmatech.BlasmaTech;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, BlasmaTech.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, BlasmaTech.MOD_ID);
    public static final DeferredRegister<Item> BLOCK_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BlasmaTech.MOD_ID);

    public static final RegistryObject<Block> POTTING_SOIL = BLOCKS.register("potting_soil",
            () -> new PottingSoil(BlockBehaviour.Properties.of(Material.DIRT, MaterialColor.DIRT).strength(0.5F).sound(SoundType.GRAVEL)
    ));

    @SuppressWarnings("DataFlowIssue")
    public static final RegistryObject<BlockEntityType<PottingSoilBE>> POTTING_SOIL_BE = BLOCK_ENTITIES.register("potting_soil",
            () -> BlockEntityType.Builder.of(PottingSoilBE::new, POTTING_SOIL.get()).build(null)
    );

    public static final RegistryObject<Item> POTTING_SOIL_ITEM = BLOCK_ITEMS.register("potting_soil",
            () -> new BlockItem(POTTING_SOIL.get(), new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS))
    );
}
