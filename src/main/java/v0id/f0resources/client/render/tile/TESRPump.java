package v0id.f0resources.client.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.animation.FastTESR;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import v0id.api.f0resources.client.model.WavefrontObject;
import v0id.f0resources.client.render.RenderUtils;
import v0id.f0resources.tile.TileFluidPump;

import java.io.IOException;

public class TESRPump extends FastTESR<TileFluidPump>
{
    public static final WavefrontObject model = new WavefrontObject();
    public static final WavefrontObject modelHead = new WavefrontObject();
    public static TextureAtlasSprite texture;

    public TESRPump()
    {
        try
        {
            model.load(Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("f0-resources", "models/block/fluid_pump.obj")).getInputStream());
            modelHead.load(Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("f0-resources", "models/block/fluid_pump_head.obj")).getInputStream());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            FMLCommonHandler.instance().raiseException(e, "Unable to load pump model", true);
        }
    }

    @Override
    public void renderTileEntityFast(TileFluidPump te, double x, double y, double z, float partialTicks, int destroyStage, float partial, BufferBuilder buffer)
    {
        if (te.isCenter)
        {
            Matrix4f transform = Matrix4f.setIdentity(new Matrix4f());
            Vector3f pos = new Vector3f((float)x + 0.5F, (float)y + 0.01F, (float)z + 0.5F);
            RenderUtils.renderObj(buffer, model, pos, transform, new float[]{ 1, 1, 1, 1 }, new int[]{ 240, 0 }, () -> texture);
            float val = te.isRotating ? (float) Math.sin(Math.toRadians(te.getWorld().getWorldTime() + partialTicks)) / 2F : 0;
            transform = transform.translate(new Vector3f(0, 2.8F, 1.1F));
            transform = transform.scale(new Vector3f(0.75F, 0.75F, 0.75F));
            transform = transform.rotate((float) Math.toRadians(90F), new Vector3f(0, 1, 0));
            transform = transform.rotate(val, new Vector3f(1, 0, 0));
            RenderUtils.renderObj(buffer, modelHead, pos, transform, new float[]{ 1, 1, 1, 1 }, new int[]{ 240, 0 }, () -> texture);
            RenderUtils.renderObj(buffer, modelHead, new Vector3f((float)x + 0.5F, (float)y + 0.01F, (float)z - 1.7F), transform, new float[]{ 1, 1, 1, 1 }, new int[]{ 240, 0 }, () -> texture);
            if (te.fluidTank.getFluid() != null)
            {
                TextureAtlasSprite tex = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(te.fluidTank.getFluid().getFluid().getStill(te.fluidTank.getFluid()).toString());
                int color = te.fluidTank.getFluid().getFluid().getColor(te.fluidTank.getFluid());
                float a = ((color & 0xFF000000) >> 24) / 255F;
                a = a <= 0.001F ? 1 : a;
                float r = ((color & 0xFF0000) >> 16) / 255F;
                float g = ((color & 0xFF00) >> 8) / 255F;
                float b = (color & 0xFF) / 255F;
                val = (float) te.fluidTank.getFluidAmount() / te.fluidTank.getCapacity();
                transform = Matrix4f.setIdentity(new Matrix4f());
                transform = transform.translate(new Vector3f(0, 0.75F + 0.75F * val, 0));
                transform = transform.scale(new Vector3f(1.49F, 1.49F * val, 1.49F));
                RenderUtils.renderCube(buffer, transform, pos, new float[]{ r, g, b, a }, new int[]{ 240, 0 }, face -> tex);
            }
        }
    }
}
