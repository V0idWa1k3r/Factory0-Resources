package v0id.f0resources.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import v0id.f0resources.F0Resources;
import v0id.f0resources.chunk.ChunkData;
import v0id.f0resources.chunk.OreData;
import v0id.f0resources.config.F0RConfig;
import v0id.f0resources.config.OreEntry;

public class MessageScan implements IMessage
{
    public boolean advanced;
    public ChunkData data;

    public MessageScan(ChunkData data, boolean advanced)
    {
        this.data = data;
        this.advanced = advanced;
    }

    public MessageScan()
    {
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.advanced = buf.readBoolean();
        this.data = new ChunkData();
        this.data.deserializeNBT(ByteBufUtils.readTag(buf));
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeBoolean(this.advanced);
        ByteBufUtils.writeTag(buf, this.data.serializeNBT());
    }

    public static class Handler implements IMessageHandler<MessageScan, IMessage>
    {
        @Override
        public IMessage onMessage(MessageScan message, MessageContext ctx)
        {
            F0Resources.proxy.getContextListener().addScheduledTask(() ->
            {
                EntityPlayer player = F0Resources.proxy.getClientPlayer();
                if (message.data.oreData.isEmpty())
                {
                    if (!F0RConfig.displayProspectorMessageInChat)
                    {
                        player.sendStatusMessage(new TextComponentTranslation("txt.f0r.prospect.nothing"), true);
                    }
                    else
                    {
                        player.sendMessage(new TextComponentTranslation("txt.f0r.prospect.nothing"));
                    }
                }
                else
                {
                    for (OreData oreData : message.data.oreData)
                    {
                        OreEntry entry = OreEntry.findByItem(oreData.oreBlock, oreData.oreMeta);
                        if (message.advanced)
                        {
                            F0Resources.proxy.addToast(new ItemStack(oreData.oreBlock, 1, oreData.oreMeta), I18n.format("txt.f0r.oreUnits", oreData.amount));
                        }
                        else
                        {
                            int index;
                            if (entry != null)
                            {
                                index = Math.max(0, Math.min(5, Math.round((float) oreData.amount / entry.oreMaximum * 6F)));
                            }
                            else
                            {
                                index = 5;
                            }

                            F0Resources.proxy.addToast(new ItemStack(oreData.oreBlock, 1, oreData.oreMeta), I18n.format("txt.f0r.prospect.nosuffix." + index));
                        }
                    }
                }


            });

            return null;
        }
    }
}
