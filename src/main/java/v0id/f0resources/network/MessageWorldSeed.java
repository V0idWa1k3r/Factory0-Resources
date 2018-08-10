package v0id.f0resources.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import v0id.f0resources.F0Resources;

public class MessageWorldSeed implements IMessage
{
    public long seed;

    public MessageWorldSeed()
    {
    }

    public MessageWorldSeed(long seed)
    {
        this.seed = seed;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.seed = buf.readLong();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeLong(this.seed);
    }

    public static class Handler implements IMessageHandler<MessageWorldSeed, IMessage>
    {
        @Override
        public IMessage onMessage(MessageWorldSeed message, MessageContext ctx)
        {
            F0Resources.proxy.storeSeed(message.seed);
            return null;
        }
    }
}
