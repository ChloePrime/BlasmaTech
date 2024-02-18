package mod.chloeprime.blasmatech.common.fluid;

import mod.chloeprime.blasmatech.BlasmaTech;
import mod.chloeprime.blasmatech.mixin.FluidAttributeBuilderAccessor;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.function.Consumer;

public class ModFluids {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, BlasmaTech.MOD_ID);
    public static final DeferredRegister<Block> FLUID_BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, BlasmaTech.MOD_ID);
    public static final DeferredRegister<Item>  BUCKETS = DeferredRegister.create(ForgeRegistries.ITEMS, BlasmaTech.MOD_ID);

    public record FluidRegisterResult(
            RegistryObject<FlowingFluid> still,
            RegistryObject<FlowingFluid> flowing,
            RegistryObject<LiquidBlock> block,
            RegistryObject<Item> bucket
    ) {}

    public static FluidRegisterResult create(String path, Consumer<FluidAttributes.Builder> attributes) {
        var props = new MutableObject<ForgeFlowingFluid.Properties>();
        var still = FLUIDS.register(path,
                () -> (FlowingFluid) new ForgeFlowingFluid.Source(props.getValue())
        );
        var flow  = FLUIDS.register("flowing_" + path,
                () -> (FlowingFluid) new ForgeFlowingFluid.Flowing(props.getValue())
        );
        var attrs = FluidAttributes.builder(
                BlasmaTech.loc("block/%s_still".formatted(path)),
                BlasmaTech.loc("block/%s_flow".formatted(path))
        );

        var lumen = Lazy.of(((FluidAttributeBuilderAccessor) attrs)::getLuminosity);
        var block = FLUID_BLOCKS.register(path, () -> new LiquidBlock(still, BlockBehaviour.Properties.of(Material.WATER, MaterialColor.WATER)
                .noCollission()
                .lightLevel(state -> lumen.get()))
        );
        var bucket = BUCKETS.register(path + "_bucket", () -> (Item) new BucketItem(still, new Item.Properties()
                .tab(CreativeModeTab.TAB_MISC)
                .craftRemainder(Items.BUCKET)
                .stacksTo(1)
        ));

        attributes.accept(attrs);
        props.setValue(new ForgeFlowingFluid.Properties(still, flow, attrs).block(block).bucket(bucket).explosionResistance(100));
        return new FluidRegisterResult(still, flow, block, bucket);
    }

    public static final FluidRegisterResult MANA_PLASMA = create("mana", attr -> attr
            .luminosity(15)
            .temperature(273)
    );
}
