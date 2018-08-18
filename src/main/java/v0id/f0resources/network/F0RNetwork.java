package v0id.f0resources.network;

import joptsimple.internal.Strings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import v0id.api.f0resources.data.F0RRegistryNames;
import v0id.api.f0resources.world.IF0RWorld;
import v0id.api.f0resources.world.IFluidData;
import v0id.api.f0resources.world.IOreData;
import v0id.f0resources.F0Resources;
import v0id.f0resources.chunk.ChunkData;
import v0id.f0resources.tile.AbstractDrill;
import v0id.f0resources.tile.IAnimated;
import v0id.f0resources.tile.TileBurnerDrill;
import v0id.f0resources.tile.TileMultiblock;

public class F0RNetwork
{
    public static final SimpleNetworkWrapper WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(F0RRegistryNames.MODID);

    static
    {
        WRAPPER.registerMessage(MessageMultiblockCenter.Handler.class, MessageMultiblockCenter.class, 0, Side.CLIENT);
        WRAPPER.registerMessage(MessageDrillItem.Handler.class, MessageDrillItem.class, 1, Side.CLIENT);
        WRAPPER.registerMessage(MessageAnimationState.Handler.class, MessageAnimationState.class, 2, Side.CLIENT);
        WRAPPER.registerMessage(MessageProspect.Handler.class, MessageProspect.class, 3, Side.CLIENT);
        WRAPPER.registerMessage(MessageDrillFuel.Handler.class, MessageDrillFuel.class, 4, Side.CLIENT);
        WRAPPER.registerMessage(MessageScan.Handler.class, MessageScan.class, 5, Side.CLIENT);
        WRAPPER.registerMessage(MessageWorldSeed.Handler.class, MessageWorldSeed.class, 6, Side.CLIENT);
        WRAPPER.registerMessage(MessageDowsing.Handler.class, MessageDowsing.class, 7, Side.CLIENT);
        WRAPPER.registerMessage(MessageFluidTank.Handler.class, MessageFluidTank.class, 8, Side.CLIENT);
    }

    public static void loadClass()
    {
    }

    public static void sendMultiblockCenter(TileMultiblock tile)
    {
        WRAPPER.sendToAllAround(new MessageMultiblockCenter(tile.getPos()), new NetworkRegistry.TargetPoint(tile.getWorld().provider.getDimension(), tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), F0Resources.proxy.getViewDistance() << 4));
    }

    public static void sendDrillItem(AbstractDrill tile)
    {
        WRAPPER.sendToAllAround(new MessageDrillItem(tile.getPos(), tile.getDrillHead()), new NetworkRegistry.TargetPoint(tile.getWorld().provider.getDimension(), tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), F0Resources.proxy.getViewDistance() << 4));
    }

    public static <T extends TileEntity & IAnimated> void sendAnimationState(T tile)
    {
        WRAPPER.sendToAllAround(new MessageAnimationState(tile.getPos(), tile.isAnimated()), new NetworkRegistry.TargetPoint(tile.getWorld().provider.getDimension(), tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), F0Resources.proxy.getViewDistance() << 4));
    }

    public static void sendDrillFuel(TileBurnerDrill tile)
    {
        WRAPPER.sendToAllAround(new MessageDrillFuel(tile.getPos(), tile.hasFuel), new NetworkRegistry.TargetPoint(tile.getWorld().provider.getDimension(), tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), F0Resources.proxy.getViewDistance() << 4));
    }

    public static void sendProspect(IOreData oreData, EntityPlayer to)
    {
        ItemStack is = oreData == null ? ItemStack.EMPTY : oreData.createOreItem(1);
        WRAPPER.sendTo(new MessageProspect(is.isEmpty() ? Strings.EMPTY : is.getItem().getRegistryName().toString(), is.getMetadata(), oreData == null ? 0 : oreData.getOreAmount()), (EntityPlayerMP) to);
    }

    public static void sendScan(EntityPlayer to, boolean advanced)
    {
        ChunkData data = (ChunkData) IF0RWorld.of(to.world).getLoadedChunkData(new ChunkPos(to.getPosition()));
        WRAPPER.sendTo(new MessageScan(data == null ? new ChunkData() : data, advanced), (EntityPlayerMP) to);
    }

    public static void sendSeed(EntityPlayer to)
    {
        WRAPPER.sendTo(new MessageWorldSeed(to.world.getSeed()), (EntityPlayerMP) to);
    }

    public static void sendDowsing(IFluidData fluidData, EntityPlayer to)
    {
        WRAPPER.sendTo(new MessageDowsing(fluidData == null ? "" : fluidData.getFluid().getName(), fluidData == null ? 0 : fluidData.getFluidAmount()), (EntityPlayerMP) to);
    }

    public static void sendFluidTank(TileEntity tile, FluidStack fluidStack)
    {
        WRAPPER.sendToAllAround(new MessageFluidTank(fluidStack, tile.getPos()), new NetworkRegistry.TargetPoint(tile.getWorld().provider.getDimension(), tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), F0Resources.proxy.getViewDistance() << 4));
    }
}
