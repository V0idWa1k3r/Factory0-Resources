package v0id.f0resources.client.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import v0id.api.f0resources.data.F0RTextures;
import v0id.f0resources.client.render.RenderUtils;
import v0id.f0resources.tile.TileFluidPump;

import java.io.IOException;

public class TESRPumpSlow extends TileEntitySpecialRenderer<TileFluidPump>
{
    public TESRPumpSlow()
    {
        try
        {
            TESRPump.model.load(Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("f0-resources", "models/block/fluid_pump.obj")).getInputStream());
            TESRPump.modelHead.load(Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("f0-resources", "models/block/fluid_pump_head.obj")).getInputStream());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            FMLCommonHandler.instance().raiseException(e, "Unable to load pump model", true);
        }
    }

    @Override
    public void render(TileFluidPump te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        if (te.isCenter)
        {
            Minecraft.getMinecraft().renderEngine.bindTexture(F0RTextures.texturePump);
            GlStateManager.pushMatrix();
            GlStateManager.disableCull();
            GlStateManager.translate(x + 0.5F, y + 0.001F, z + 0.5F);
            BufferBuilder bb = Tessellator.getInstance().getBuffer();
            bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
            TESRPump.model.putVertices(bb);
            Tessellator.getInstance().draw();
            GlStateManager.enableCull();
            float val = te.isRotating ? (float) Math.sin(Math.toRadians(te.getWorld().getWorldTime() + partialTicks)) / 2F : 0;
            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 2.8F, 1.1F);
            GlStateManager.scale(0.75F, 0.75F, 0.75F);
            GlStateManager.rotate(90, 0, 1, 0);
            GlStateManager.rotate(val * 90, 1, 0, 0);
            bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
            TESRPump.modelHead.putVertices(bb);
            Tessellator.getInstance().draw();
            GlStateManager.translate(2.9F, 0, 0);
            bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
            TESRPump.modelHead.putVertices(bb);
            Tessellator.getInstance().draw();
            GlStateManager.popMatrix();
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.disableCull();
            if (te.fluidTank.getFluid() != null)
            {
                Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                TextureAtlasSprite tex = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(te.fluidTank.getFluid().getFluid().getStill(te.fluidTank.getFluid()).toString());
                int color = te.fluidTank.getFluid().getFluid().getColor(te.fluidTank.getFluid());
                float a = ((color & 0xFF000000) >> 24) / 255F;
                a = a <= 0.001F ? 1 : a;
                float r = ((color & 0xFF0000) >> 16) / 255F;
                float g = ((color & 0xFF00) >> 8) / 255F;
                float b = (color & 0xFF) / 255F;
                val = (float) te.fluidTank.getFluidAmount() / te.fluidTank.getCapacity();
                Matrix4f transform = Matrix4f.setIdentity(new Matrix4f());
                Vector3f pos = new Vector3f(0, 0, 0);
                transform = transform.translate(new Vector3f(0, 0.75F + 0.75F * val, 0));
                transform = transform.scale(new Vector3f(1.49F, 1.49F * val, 1.49F));
                bb.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
                RenderUtils.renderCube(bb, transform, pos, new float[]{ r, g, b, a }, new int[]{ 240, 0 }, face -> tex);
                Tessellator.getInstance().draw();
            }

            GlStateManager.enableCull();
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }
}
