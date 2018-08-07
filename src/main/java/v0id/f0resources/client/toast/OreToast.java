package v0id.f0resources.client.toast;

import net.minecraft.client.gui.toasts.GuiToast;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class OreToast implements IToast
{
    public ItemStack ore;
    public String text;

    public OreToast(ItemStack ore, String text)
    {
        this.ore = ore;
        this.text = text;
    }

    @Override
    public Visibility draw(GuiToast toastGui, long delta)
    {
        toastGui.getMinecraft().getTextureManager().bindTexture(TEXTURE_TOASTS);
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        toastGui.drawTexturedModalRect(0, 0, 0, 0, 160, 32);
        toastGui.getMinecraft().fontRenderer.drawString(this.ore.getDisplayName(), 30, 7, 0xffffff);
        toastGui.getMinecraft().fontRenderer.drawString(text, 30, 18, 0xffffff);
        RenderHelper.enableGUIStandardItemLighting();
        toastGui.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI((EntityLivingBase)null, this.ore, 8, 8);
        return delta >= 5000L ? IToast.Visibility.HIDE : IToast.Visibility.SHOW;
    }
}
