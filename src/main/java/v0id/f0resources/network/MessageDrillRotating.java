package v0id.f0resources.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import v0id.f0resources.F0Resources;
import v0id.f0resources.tile.TileDrill;

public class MessageDrillRotating implements IMessage
{
    public BlockPos at;
    public boolean isRotating;

    public MessageDrillRotating()
    {
    }

    public MessageDrillRotating(BlockPos at, boolean b)
    {
        this.at = at;
        this.isRotating = b;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.at = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        this.isRotating = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.at.getX());
        buf.writeInt(this.at.getY());
        buf.writeInt(this.at.getZ());
        buf.writeBoolean(this.isRotating);
    }

    public static class Handler implements IMessageHandler<MessageDrillRotating, IMessage>
    {
        @Override
        public IMessage onMessage(MessageDrillRotating message, MessageContext ctx)
        {
            F0Resources.proxy.getContextListener().addScheduledTask(() ->
            {
                World world = F0Resources.proxy.getClientWorld();
                BlockPos pos = message.at;
                if (world.isBlockLoaded(pos))
                {
                    TileEntity tile = world.getTileEntity(pos);
                    if (tile instanceof TileDrill)
                    {
                        ((TileDrill) tile).isRotating = message.isRotating;
                    }
                }
            });

            return null;
        }
    }
}
