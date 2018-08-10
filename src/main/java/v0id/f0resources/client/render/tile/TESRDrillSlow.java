package v0id.f0resources.client.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.lwjgl.opengl.GL11;
import v0id.api.f0resources.client.model.WavefrontObject;
import v0id.api.f0resources.data.F0RTextures;
import v0id.f0resources.config.DrillMaterialEntry;
import v0id.f0resources.item.ItemDrillHead;
import v0id.f0resources.tile.TileDrill;

import java.io.IOException;

public class TESRDrillSlow extends TileEntitySpecialRenderer<TileDrill>
{
    public TESRDrillSlow()
    {
        try
        {
            TESRDrill.model.load(Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("f0-resources", "models/block/drill.obj")).getInputStream());
            TESRDrill.modelHead.load(Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("f0-resources", "models/block/drill_head.obj")).getInputStream());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            FMLCommonHandler.instance().raiseException(e, "Unable to load drill model", true);
        }
    }

    @Override
    public void render(TileDrill te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        if (te.isCenter)
        {
            Minecraft.getMinecraft().renderEngine.bindTexture(F0RTextures.textureDrill);
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.5F, y, z + 0.5F);
            GlStateManager.scale(0.75F, 1F, 0.75F);
            BufferBuilder bb = Tessellator.getInstance().getBuffer();
            bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
            TESRDrill.model.putVertices(bb);
            Tessellator.getInstance().draw();
            ItemStack is = te.inventory.getStackInSlot(0);
            if (!is.isEmpty() && is.getItem() instanceof ItemDrillHead)
            {
                float rotation = te.isRotating ? (te.getWorld().getWorldTime() % 45) * 8F + partialTicks * 8 : 0F;
                DrillMaterialEntry materialEntry = ((ItemDrillHead) is.getItem()).material;
                float r = ((materialEntry.color & 0xff0000) >> 16) / 255F;
                float g = ((materialEntry.color & 0xff00) >> 8) / 255F;
                float b = (materialEntry.color & 0xff) / 255F;
                GlStateManager.scale(0.75F, 0.75F, 0.75F);
                GlStateManager.rotate(rotation, 0, 1, 0);
                Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("minecraft", "textures/blocks/iron_block.png"));
                bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
                for (WavefrontObject.Vertex vertex : TESRDrill.modelHead.getVertices())
                {
                    bb.pos(vertex.position.x, vertex.position.y, vertex.position.z).tex(vertex.uvset.x, 1 - vertex.uvset.y).color(r, g, b, 1F).normal(vertex.normals.x, vertex.normals.y, vertex.normals.z).endVertex();
                }

                Tessellator.getInstance().draw();
            }

            GlStateManager.popMatrix();
        }
    }
}
