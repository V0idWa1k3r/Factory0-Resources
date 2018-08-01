package v0id.f0resources.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import v0id.f0resources.F0Resources;
import v0id.f0resources.tile.TileDrill;

public class MessageDrillItem implements IMessage
{
    public BlockPos at;
    public ItemStack stack;

    public MessageDrillItem(BlockPos pos, ItemStack stack)
    {
        this.at = pos;
        this.stack = stack;
    }

    public MessageDrillItem()
    {
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.at = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        stack = new ItemStack(ByteBufUtils.readTag(buf));
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.at.getX());
        buf.writeInt(this.at.getY());
        buf.writeInt(this.at.getZ());
        ByteBufUtils.writeTag(buf, stack.serializeNBT());
    }

    public static class Handler implements IMessageHandler<MessageDrillItem, IMessage>
    {
        @Override
        public IMessage onMessage(MessageDrillItem message, MessageContext ctx)
        {
            F0Resources.proxy.getContextListener().addScheduledTask(() ->
            {
                World world = F0Resources.proxy.getClientWorld();
                if (world.isBlockLoaded(message.at))
                {
                    TileEntity tile = world.getTileEntity(message.at);
                    if (tile instanceof TileDrill)
                    {
                        ((TileDrill) tile).inventory.setStackInSlot(0, message.stack);
                    }
                }
            });

            return null;
        }
    }
}
