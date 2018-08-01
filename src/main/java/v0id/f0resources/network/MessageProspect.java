package v0id.f0resources.network;

import com.google.common.base.Strings;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.registry.GameRegistry;
import v0id.f0resources.F0Resources;
import v0id.f0resources.config.OreEntry;

public class MessageProspect implements IMessage
{
    public String name;
    public int meta;
    public int amount;

    public MessageProspect(String name, int meta, int amount)
    {
        this.name = name;
        this.meta = meta;
        this.amount = amount;
    }

    public MessageProspect()
    {
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.name = ByteBufUtils.readUTF8String(buf);
        this.meta = buf.readShort();
        this.amount = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, this.name);
        buf.writeShort(this.meta);
        buf.writeInt(this.amount);
    }

    public static class Handler implements IMessageHandler<MessageProspect, IMessage>
    {
        @Override
        public IMessage onMessage(MessageProspect message, MessageContext ctx)
        {
            F0Resources.proxy.getContextListener().addScheduledTask(() ->
            {
                EntityPlayer player = F0Resources.proxy.getClientPlayer();
                ITextComponent toSend;
                if (Strings.isNullOrEmpty(message.name))
                {
                    toSend = new TextComponentTranslation("txt.f0r.prospect.nothing");
                }
                else
                {
                    Item ore = GameRegistry.findRegistry(Item.class).getValue(new ResourceLocation(message.name));
                    if (ore != null)
                    {
                        OreEntry entry = OreEntry.findByItem(ore, message.meta);
                        int index;
                        if (entry != null)
                        {
                            index = Math.max(0, Math.min(5, Math.round((float) message.amount / entry.oreMaximum * 6F)));
                        }
                        else
                        {
                            index = 5;
                        }

                        toSend = new TextComponentTranslation("txt.f0r.prospect." + index).appendSibling(new TextComponentString(" ")).appendSibling(new TextComponentString(new ItemStack(ore, 1, message.meta).getDisplayName()));
                    }
                    else
                    {
                        toSend = new TextComponentTranslation("txt.f0r.prospect.nothing");
                    }
                }

                player.sendMessage(toSend);
            });

            return null;
        }
    }
}
