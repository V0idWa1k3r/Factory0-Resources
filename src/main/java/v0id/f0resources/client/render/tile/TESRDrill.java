package v0id.f0resources.client.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.animation.FastTESR;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import v0id.api.f0resources.client.model.WavefrontObject;
import v0id.f0resources.client.render.RenderUtils;
import v0id.f0resources.config.DrillMaterialEntry;
import v0id.f0resources.item.ItemDrillHead;
import v0id.f0resources.tile.TileDrill;

import java.io.IOException;

public class TESRDrill extends FastTESR<TileDrill>
{
    public static final WavefrontObject model = new WavefrontObject();
    public static final WavefrontObject modelHead = new WavefrontObject();
    public static TextureAtlasSprite texture;

    public TESRDrill()
    {
        try
        {
            model.load(Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("f0-resources", "models/block/drill.obj")).getInputStream());
            modelHead.load(Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("f0-resources", "models/block/drill_head.obj")).getInputStream());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            FMLCommonHandler.instance().raiseException(e, "Unable to load drill model", true);
        }
    }

    @Override
    public void renderTileEntityFast(TileDrill te, double x, double y, double z, float partialTicks, int destroyStage, float partial, BufferBuilder buffer)
    {
        if (te.isCenter)
        {
            Matrix4f transform = Matrix4f.setIdentity(new Matrix4f());
            transform = transform.scale(new Vector3f(0.75F, 1, 0.75F));
            Vector3f pos = new Vector3f((float)x + 0.5F, (float)y, (float)z + 0.5F);
            RenderUtils.renderObj(buffer, model, pos, transform, new float[]{ 1, 1, 1, 1 }, new int[]{ 240, 0 }, () -> texture);
            ItemStack is = te.inventory.getStackInSlot(0);
            if (!is.isEmpty() && is.getItem() instanceof ItemDrillHead)
            {
                float rotation = te.isRotating ? (float)Math.toRadians((te.getWorld().getWorldTime() % 45) * 8F + partialTicks * 8) : 0F;
                transform = Matrix4f.setIdentity(new Matrix4f());
                transform = transform.scale(new Vector3f(0.55F, 1, 0.55F));
                transform = transform.rotate(rotation, new Vector3f(0, 1, 0));
                pos = new Vector3f((float)x + 0.5F, (float)y, (float)z + 0.5F);
                DrillMaterialEntry materialEntry = ((ItemDrillHead) is.getItem()).material;
                float r = ((materialEntry.color & 0xff0000) >> 16) / 255F;
                float g = ((materialEntry.color & 0xff00) >> 8) / 255F;
                float b = (materialEntry.color & 0xff) / 255F;
                RenderUtils.renderObj(buffer, modelHead, pos, transform, new float[]{ r, g, b, 1 }, new int[]{ 240, 0 }, () -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("minecraft:blocks/iron_block"));
            }
        }
    }
}
