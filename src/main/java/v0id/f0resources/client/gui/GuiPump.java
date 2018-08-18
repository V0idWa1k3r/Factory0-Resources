package v0id.f0resources.client.gui;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import org.lwjgl.opengl.GL11;
import v0id.api.f0resources.data.F0RTextures;
import v0id.f0resources.inventory.ContainerPump;
import v0id.f0resources.tile.TileFluidPump;

import java.util.List;

public class GuiPump extends GuiContainer
{
    public TileFluidPump tile;

    public GuiPump(InventoryPlayer playerInv, TileFluidPump drill)
    {
        super(new ContainerPump(playerInv, drill));
        this.tile = drill;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().renderEngine.bindTexture(F0RTextures.guiPump);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
        float powerValue = (float)this.tile.energyStorage.getEnergyStored() / this.tile.energyStorage.getMaxEnergyStored();
        this.drawTexturedModalRect(i + 14, j + 15 + (int)((1 - powerValue) * 62), 176, (int) ((1 - powerValue) * 62), 4, (int) (powerValue * 62));
        if (this.tile.fluidTank.getFluid() != null)
        {
            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            TextureAtlasSprite tex = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(this.tile.fluidTank.getFluid().getFluid().getStill(this.tile.fluidTank.getFluid()).toString());
            float val = (float) this.tile.fluidTank.getFluidAmount() / this.tile.fluidTank.getCapacity();
            int color = this.tile.fluidTank.getFluid().getFluid().getColor(this.tile.fluidTank.getFluid());
            float a = ((color & 0xFF000000) >> 24) / 255F;
            a = a <= 0.001F ? 1 : a;
            float r = ((color & 0xFF0000) >> 16) / 255F;
            float g = ((color & 0xFF00) >> 8) / 255F;
            float b = (color & 0xFF) / 255F;
            BufferBuilder buf = Tessellator.getInstance().getBuffer();
            buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            buf.pos(i + 32, j + 15 + (1 - val) * 62, 0).tex(tex.getMinU(), tex.getInterpolatedV(val)).color(r, g, b, a).endVertex();
            buf.pos(i + 32, j + 77, 0).tex(tex.getMinU(), tex.getMaxV()).color(r, g, b, a).endVertex();
            buf.pos(i + 36, j + 77, 0).tex(tex.getMaxU(), tex.getMaxV()).color(r, g, b, a).endVertex();
            buf.pos(i + 36, j + 15 + (1 - val) * 62, 0).tex(tex.getMaxU(), tex.getInterpolatedV(val)).color(r, g, b, a).endVertex();
            Tessellator.getInstance().draw();
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        if (mouseX >= i + 7 && mouseX <= i + 25 && mouseY >= j + 7 && mouseY <= j + 83)
        {
            List<String> lines = Lists.newArrayList();
            lines.add(I18n.format("txt.f0r.rfStored", this.tile.energyStorage.getEnergyStored(), this.tile.energyStorage.getMaxEnergyStored()));
            this.drawHoveringText(lines, mouseX, mouseY);
        }

        if (mouseX >= i + 25 && mouseX <= i + 43 && mouseY >= j + 7 && mouseY <= j + 83)
        {
            List<String> lines = Lists.newArrayList();
            lines.add(I18n.format("txt.f0r.mbStored", this.tile.fluidTank.getFluidAmount(), this.tile.fluidTank.getCapacity()));
            this.drawHoveringText(lines, mouseX, mouseY);
        }
    }
}

