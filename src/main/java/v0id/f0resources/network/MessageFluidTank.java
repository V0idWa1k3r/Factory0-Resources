package v0id.f0resources.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import v0id.f0resources.F0Resources;

public class MessageFluidTank implements IMessage
{
    public FluidStack fluidData;
    public BlockPos at;

    public MessageFluidTank(FluidStack fluidData, BlockPos at)
    {
        this.fluidData = fluidData;
        this.at = at;
    }

    public MessageFluidTank()
    {
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.at = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        this.fluidData = FluidRegistry.getFluidStack(ByteBufUtils.readUTF8String(buf), buf.readInt());
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.at.getX());
        buf.writeInt(this.at.getY());
        buf.writeInt(this.at.getZ());
        ByteBufUtils.writeUTF8String(buf, this.fluidData == null ? "" : this.fluidData.getFluid().getName());
        buf.writeInt(this.fluidData == null ? 0 : this.fluidData.amount);
    }

    public static class Handler implements IMessageHandler<MessageFluidTank, IMessage>
    {
        @Override
        public IMessage onMessage(MessageFluidTank message, MessageContext ctx)
        {
            F0Resources.proxy.getContextListener().addScheduledTask(() ->
            {
                World world = F0Resources.proxy.getClientWorld();
                if (world.isBlockLoaded(message.at))
                {
                    TileEntity tile = world.getTileEntity(message.at);
                    if (tile != null)
                    {
                        IFluidHandler tank = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
                        if (tank != null)
                        {
                            tank.drain(Integer.MAX_VALUE, true);
                            tank.fill(message.fluidData, true);
                        }
                    }
                }
            });

            return null;
        }
    }
}
