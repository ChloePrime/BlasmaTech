package mod.chloeprime.blasmatech.client.particle;

import mod.chloeprime.blasmatech.common.particle.FluidParticleData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;

public class FluidStackParticle extends TextureSheetParticle {
    private final float uo;
    private final float vo;
    private final FluidStack fluid;

    public static FluidStackParticle create(ParticleType<FluidParticleData> type, ClientLevel world, FluidStack fluid, double x,
                                            double y, double z, double vx, double vy, double vz) {
        return new FluidStackParticle(world, fluid, x, y, z, vx, vy, vz);
    }

    public FluidStackParticle(ClientLevel world, FluidStack fluid, double x, double y, double z, double vx, double vy,
                              double vz) {
        super(world, x, y, z, vx, vy, vz);
        this.fluid = fluid;
        this.setSprite(Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(fluid.getFluid()
                        .getAttributes()
                        .getStillTexture()));

        this.gravity = 1.0F;
        this.rCol = 0.8F;
        this.gCol = 0.8F;
        this.bCol = 0.8F;
        this.multiplyColor(fluid.getFluid()
                .getAttributes()
                .getColor(fluid));

        this.xd = vx;
        this.yd = vy;
        this.zd = vz;

        this.quadSize /= 2.0F;
        this.uo = this.random.nextFloat() * 3.0F;
        this.vo = this.random.nextFloat() * 3.0F;
    }

    @Override
    protected int getLightColor(float p_189214_1_) {
        int brightnessForRender = super.getLightColor(p_189214_1_);
        int skyLight = brightnessForRender >> 20;
        int blockLight = (brightnessForRender >> 4) & 0xf;
        blockLight = Math.max(blockLight, fluid.getFluid()
                .getAttributes()
                .getLuminosity(fluid));
        return (skyLight << 20) | (blockLight << 4);
    }

    protected void multiplyColor(int color) {
        this.rCol *= (float) (color >> 16 & 255) / 255.0F;
        this.gCol *= (float) (color >> 8 & 255) / 255.0F;
        this.bCol *= (float) (color & 255) / 255.0F;
    }

    protected float getU0() {
        return this.sprite.getU((this.uo + 1.0F) / 4.0F * 16.0F);
    }

    protected float getU1() {
        return this.sprite.getU(this.uo / 4.0F * 16.0F);
    }

    protected float getV0() {
        return this.sprite.getV(this.vo / 4.0F * 16.0F);
    }

    protected float getV1() {
        return this.sprite.getV((this.vo + 1.0F) / 4.0F * 16.0F);
    }

    @Nonnull
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.TERRAIN_SHEET;
    }
}