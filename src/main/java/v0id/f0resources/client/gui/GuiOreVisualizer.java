package v0id.f0resources.client.gui;

import com.google.common.collect.Lists;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.lwjgl.opengl.GL11;
import v0id.api.f0resources.data.F0RTextures;
import v0id.f0resources.chunk.ChunkData;
import v0id.f0resources.chunk.ChunkOreGenerator;
import v0id.f0resources.chunk.FluidData;
import v0id.f0resources.chunk.OreData;
import v0id.f0resources.config.FluidEntry;
import v0id.f0resources.config.OreEntry;
import v0id.f0resources.inventory.ContainerOreVisualizer;

import java.util.List;

public class GuiOreVisualizer extends GuiContainer
{
    public static long worldSeed;

    public OreEntry entry;
    public FluidEntry fluidEntry;
    public ChunkUIData[][] uiArray = new ChunkUIData[15][15];
    public int chunkIndex = 0;
    public ItemStack lastItemStack = ItemStack.EMPTY;

    public GuiOreVisualizer(InventoryPlayer playerInv)
    {
        super(new ContainerOreVisualizer(playerInv));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().renderEngine.bindTexture(F0RTextures.guiVisualizer);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
        GlStateManager.disableTexture2D();
        GlStateManager.color(1F, 1F, 1F);
        BufferBuilder buf = Tessellator.getInstance().getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        for (int x = 0; x < 15; ++x)
        {
            for (int z = 0; z < 15; ++z)
            {
                ChunkUIData data = this.uiArray[x][z];
                if (data != null)
                {
                    for (int k = 0; k < 16; ++k)
                    {
                        int c = data.buffer[k];
                        int r = (c & 0xFF0000) >> 16;
                        int g = (c & 0xFF00) >> 8;
                        int b = c & 0xFF;
                        buf.pos(i + 58 + (x << 2) + k % 4, j + 15 + (z << 2) + (k >> 2), 0).color(r, g, b, 255).endVertex();
                        buf.pos(i + 58 + (x << 2) + k % 4, j + 15 + (z << 2) + (k >> 2) + 1, 0).color(r, g, b, 255).endVertex();
                        buf.pos(i + 58 + (x << 2) + k % 4 + 1, j + 15 + (z << 2) + (k >> 2) + 1, 0).color(r, g, b, 255).endVertex();
                        buf.pos(i + 58 + (x << 2) + k % 4 + 1, j + 15 + (z << 2) + (k >> 2), 0).color(r, g, b, 255).endVertex();
                    }

                    if (data.oreAmount != 0 && this.entry != null)
                    {
                        float val =  (float)data.oreAmount / this.entry.oreMaximum;
                        buf.pos(i + 58 + (x << 2), j + 15 + (z << 2), 1).color(1F, 1F, 1F, val).endVertex();
                        buf.pos(i + 58 + (x << 2), j + 15 + (z << 2) + 4, 1).color(1F, 1F, 1F, val).endVertex();
                        buf.pos(i + 58 + (x << 2) + 4, j + 15 + (z << 2) + 4, 1).color(1F, 1F, 1F, val).endVertex();
                        buf.pos(i + 58 + (x << 2) + 4, j + 15 + (z << 2), 1).color(1F, 1F, 1F, val).endVertex();
                    }

                    if (data.oreAmount != 0 && this.fluidEntry != null)
                    {
                        float val =  (float)data.oreAmount / this.fluidEntry.fluidMaximum;
                        buf.pos(i + 58 + (x << 2), j + 15 + (z << 2), 1).color(0F, 0F, 1F, val).endVertex();
                        buf.pos(i + 58 + (x << 2), j + 15 + (z << 2) + 4, 1).color(0F, 0F, 1F, val).endVertex();
                        buf.pos(i + 58 + (x << 2) + 4, j + 15 + (z << 2) + 4, 1).color(0F, 1F, 1F, val).endVertex();
                        buf.pos(i + 58 + (x << 2) + 4, j + 15 + (z << 2), 1).color(0F, 0F, 1F, val).endVertex();
                    }
                }
            }
        }

        Tessellator.getInstance().draw();
        GlStateManager.enableTexture2D();
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        if (mouseX >= i + 58 && mouseX <= i + 118 && mouseY > j + 15 && mouseY <= j + 75)
        {
            int px = Math.max(0, Math.min(14, (mouseX - (i + 58)) >> 2));
            int pz = Math.max(0, Math.min(14, (mouseY - (j + 15)) >> 2));
            ChunkUIData data = this.uiArray[px][pz];
            if (data != null)
            {
                ChunkPos cPos = new ChunkPos(Minecraft.getMinecraft().player.getPosition());
                cPos = new ChunkPos(cPos.x + (px - 7), cPos.z + (pz - 7));
                ChunkPos playerPos = new ChunkPos(Minecraft.getMinecraft().player.getPosition());
                List<String> lst = Lists.newArrayList();
                lst.add(I18n.format("commands.chunkinfo.location", cPos.x, 0, cPos.z));
                if (px == 7 && pz == 7)
                {
                    lst.add(I18n.format("txr.f0r.youAreHere"));
                }

                if (this.entry != null)
                {
                    lst.add(I18n.format("txr.f0r.visualizer_amt", data.oreAmount, entry.oreMaximum));
                }

                if (this.fluidEntry != null)
                {
                    lst.add(I18n.format("txr.f0r.visualizer_amt", data.oreAmount, this.fluidEntry.fluidMaximum));
                }

                this.drawHoveringText(lst, mouseX, mouseY);
            }
        }
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
        if (this.chunkIndex < 15 * 15)
        {
            int x = this.chunkIndex % 15;
            int z = this.chunkIndex / 15;
            this.uiArray[x][z] = createUIData(x - 7, z - 7);
            ++this.chunkIndex;
        }

        ItemStack current = ((ContainerOreVisualizer)this.inventorySlots).inventory.getStackInSlot(0);
        if (current != this.lastItemStack)
        {
            OreEntry entry = null;
            FluidEntry fluidEntry = null;
            if (!current.isEmpty())
            {
                entry = OreEntry.findByItem(current.getItem(), current.getMetadata());
                IFluidHandlerItem itemFluidHandler = current.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
                if (itemFluidHandler != null && itemFluidHandler.getTankProperties().length > 0 && itemFluidHandler.getTankProperties()[0].getContents() != null)
                {
                    fluidEntry = FluidEntry.findByFluid(itemFluidHandler.getTankProperties()[0].getContents().getFluid());
                }
            }

            if (entry != this.entry)
            {
                this.entry = entry;
                this.createOresForData();
            }
            else
            {
                if (fluidEntry != this.fluidEntry)
                {
                    this.fluidEntry = fluidEntry;
                    this.createOresForData();
                }
            }
        }

        this.lastItemStack = current;
    }

    public ChunkUIData createUIData(int x, int z)
    {
        Item item = this.entry == null ? null : GameRegistry.findRegistry(Item.class).getValue(new ResourceLocation(this.entry.oreID));
        World w = Minecraft.getMinecraft().world;
        ChunkPos cPos = new ChunkPos(Minecraft.getMinecraft().player.getPosition());
        cPos = new ChunkPos(cPos.x + x, cPos.z + z);
        BlockPos pos = new BlockPos(cPos.x << 4, 0, cPos.z << 4);
        if (w.isBlockLoaded(pos))
        {
            int[] buffer = new int[16];
            for (int dx = 0; dx < 4; ++dx)
            {
                for (int dz = 0; dz < 4; ++dz)
                {
                    BlockPos at = w.getHeight(pos.add(dx << 2, 0, dz << 2)).down();
                    MapColor color = w.getBlockState(at).getMapColor(w, at);
                    buffer[dx * 4 + dz] = color.getMapColor(color.colorIndex);
                }
            }

            long value = 0;
            if (this.entry != null || this.fluidEntry != null)
            {
                ChunkData dummy = new ChunkData();
                ChunkOreGenerator.generateData(worldSeed, w.provider.getDimension(), cPos, dummy);
                if (this.entry != null)
                {
                    for (OreData dat : dummy.oreData)
                    {
                        if (dat.oreBlock == item && dat.oreMeta == this.entry.oreMeta)
                        {
                            value = dat.amount;
                            break;
                        }
                    }
                }

                if (this.fluidEntry != null)
                {
                    for (FluidData dat : dummy.fluidData)
                    {
                        if (dat.fluid.getName().equalsIgnoreCase(this.fluidEntry.fluidID))
                        {
                            value = dat.amount;
                            break;
                        }
                    }
                }
            }

            return new ChunkUIData(buffer, value);
        }

        return null;
    }

    public void createOresForData()
    {
        Item item = this.entry == null ? null : GameRegistry.findRegistry(Item.class).getValue(new ResourceLocation(this.entry.oreID));
        for (int dx = 0; dx < 15; ++dx)
        {
            for (int dz = 0; dz < 15; ++dz)
            {
                ChunkUIData data = this.uiArray[dx][dz];
                if (data != null)
                {
                    if (this.entry == null && this.fluidEntry == null)
                    {
                        data.oreAmount = 0;
                    }
                    else
                    {
                        World w = Minecraft.getMinecraft().world;
                        ChunkPos cPos = new ChunkPos(Minecraft.getMinecraft().player.getPosition());
                        cPos = new ChunkPos(cPos.x + (dx - 7), cPos.z + (dz - 7));
                        ChunkData dummy = new ChunkData();
                        ChunkOreGenerator.generateData(worldSeed, w.provider.getDimension(), cPos, dummy);
                        if (this.entry != null)
                        {
                            for (OreData dat : dummy.oreData)
                            {
                                if (dat.oreBlock == item && dat.oreMeta == this.entry.oreMeta)
                                {
                                    data.oreAmount = dat.amount;
                                    break;
                                }
                            }
                        }
                        else
                        {
                            if (this.fluidEntry != null)
                            {
                                for (FluidData dat : dummy.fluidData)
                                {
                                    if (dat.fluid.getName().equalsIgnoreCase(this.fluidEntry.fluidID))
                                    {
                                        data.oreAmount = dat.amount;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static class ChunkUIData
    {
        public int[] buffer;
        public long oreAmount;

        public ChunkUIData(int[] buffer, long oreAmount)
        {
            this.buffer = buffer;
            this.oreAmount = oreAmount;
        }
    }
}

