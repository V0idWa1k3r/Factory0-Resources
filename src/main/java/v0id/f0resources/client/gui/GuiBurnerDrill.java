package v0id.f0resources.client.gui;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import v0id.api.f0resources.data.F0RTextures;
import v0id.f0resources.inventory.ContainerBurnerDrill;
import v0id.f0resources.tile.TileBurnerDrill;

import java.util.List;

public class GuiBurnerDrill extends GuiContainer
{
    public TileBurnerDrill tile;

    public GuiBurnerDrill(InventoryPlayer playerInv, TileBurnerDrill drill)
    {
        super(new ContainerBurnerDrill(playerInv, drill));
        this.tile = drill;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().renderEngine.bindTexture(F0RTextures.guiBurnerDrill);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
        float fuelValue = (float)this.tile.fuelHandler.getFuel() / this.tile.fuelHandler.getMaxFuel();
        this.drawTexturedModalRect(i + 80, j + 36 + (int)((1 - fuelValue) * 18), 176, (int) ((1 - fuelValue) * 18), 18, (int) (fuelValue * 18));
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        if (mouseX >= i + 80 && mouseX <= i + 98 && mouseY >= j + 34 && mouseY <= j + 51)
        {
            List<String> lines = Lists.newArrayList();
            lines.add(I18n.format("txt.f0r.fuelStored", this.tile.fuelHandler.getFuel(), this.tile.fuelHandler.getMaxFuel()));
            this.drawHoveringText(lines, mouseX, mouseY);
        }
    }
}

