package v0id.f0resources.network;

import com.google.common.base.Strings;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import v0id.f0resources.F0Resources;
import v0id.f0resources.config.F0RConfig;
import v0id.f0resources.config.FluidEntry;

public class MessageDowsing implements IMessage
{
    public String name;
    public long amount;

    public MessageDowsing(String name, long amount)
    {
        this.name = name;
        this.amount = amount;
    }

    public MessageDowsing()
    {
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.name = ByteBufUtils.readUTF8String(buf);
        this.amount = buf.readLong();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, this.name);
        buf.writeLong(this.amount);
    }

    public static class Handler implements IMessageHandler<MessageDowsing, IMessage>
    {
        @Override
        public IMessage onMessage(MessageDowsing message, MessageContext ctx)
        {
            F0Resources.proxy.getContextListener().addScheduledTask(() ->
            {
                EntityPlayer player = F0Resources.proxy.getClientPlayer();
                ITextComponent toSend;
                if (Strings.isNullOrEmpty(message.name))
                {
                    toSend = new TextComponentTranslation("txt.f0r.dowsing.nothing");
                }
                else
                {
                    Fluid fluid = FluidRegistry.getFluid(message.name);
                    if (fluid != null)
                    {
                        FluidEntry entry = FluidEntry.findByFluid(fluid);
                        int index;
                        if (entry != null)
                        {
                            index = Math.max(0, Math.min(5, Math.round((float) message.amount / entry.fluidMaximum * 6F)));
                        }
                        else
                        {
                            index = 5;
                        }

                        toSend = new TextComponentTranslation("txt.f0r.dowsing." + index).appendSibling(new TextComponentString(" ")).appendSibling(new TextComponentString(new FluidStack(fluid, 1).getLocalizedName()));
                    }
                    else
                    {
                        toSend = new TextComponentTranslation("txt.f0r.dowsing.nothing");
                    }
                }

                if (!F0RConfig.displayProspectorMessageInChat)
                {
                    player.sendStatusMessage(toSend, true);
                }
                else
                {
                    player.sendMessage(toSend);
                }
            });

            return null;
        }
    }
}
