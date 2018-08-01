package v0id.f0resources.server;

import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import v0id.api.f0resources.world.IChunkData;
import v0id.api.f0resources.world.IF0RWorld;
import v0id.api.f0resources.world.IOreData;
import v0id.f0resources.chunk.OreData;

public class CommandF0R extends CommandBase
{
    @Override
    public String getName()
    {
        return "f0r";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return I18n.format("txt.f0r.command.usage");
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length == 0)
        {
            throw new WrongUsageException("txt.f0r.command.noArgs");
        }
        else
        {
            String arg = args[0];
            if (arg.equalsIgnoreCase("help"))
            {
                if (args.length == 1)
                {
                    throw new WrongUsageException("txt.f0r.command.help.noArgs");
                }
                else
                {
                    if (args[1].equalsIgnoreCase("help") || args[1].equalsIgnoreCase("query") || args[1].equalsIgnoreCase("set") || args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("add"))
                    {
                        sender.sendMessage(new TextComponentTranslation("txt.f0r.command.help." + args[1]));
                    }
                    else
                    {
                        throw new WrongUsageException("txt.f0r.command.help.unrecognizedCommand");
                    }
                }
            }
            else
            {
                if (arg.equalsIgnoreCase("query"))
                {
                    ChunkPos position = new ChunkPos(sender.getPosition());
                    if (args.length >= 2)
                    {
                        String[] cposstring = args[1].split(",");
                        try
                        {
                            position = new ChunkPos(Integer.parseInt(cposstring[0]), Integer.parseInt(cposstring[1]));
                        }
                        catch (Exception ex)
                        {
                            throw new WrongUsageException("txt.f0r.command.query.invalidPos");
                        }
                    }

                    IF0RWorld world = IF0RWorld.of(sender.getEntityWorld());
                    IChunkData data = world.getLoadedChunkData(position);
                    if (data == null)
                    {
                        throw new WrongUsageException("txt.f0r.command.query.noData");
                    }
                    else
                    {
                        for (IOreData oreData : data)
                        {
                            sender.sendMessage(new TextComponentTranslation(oreData.createOreItem(1).getUnlocalizedName() + ".name").appendSibling(new TextComponentTranslation("txt.f0r.command.query.data.amount", oreData.getOreAmount())));
                        }
                    }
                }
                else
                {
                    if (arg.equalsIgnoreCase("set"))
                    {
                        if (args.length < 3)
                        {
                            throw new WrongUsageException("txt.f0r.command.set.notEnoughArgs");
                        }

                        int index;
                        int amount;
                        try
                        {
                            index = Integer.parseInt(args[1]);
                            amount = Integer.parseInt(args[2]);
                        }
                        catch (NumberFormatException ex)
                        {
                            throw new WrongUsageException("txt.f0r.command.set.invalidParams");
                        }

                        IF0RWorld world = IF0RWorld.of(sender.getEntityWorld());
                        IChunkData data = world.getLoadedChunkData(new ChunkPos(sender.getPosition()));
                        if (data == null)
                        {
                            throw new WrongUsageException("txt.f0r.command.set.nullData");
                        }

                        if (data.getSize() == 0)
                        {
                            throw new WrongUsageException("txt.f0r.command.set.noData");
                        }

                        if (index >= data.getSize() || index < 0)
                        {
                            throw new WrongUsageException("txt.f0r.command.set.indexTooBig", index - 1);
                        }

                        IOreData oreData = data.getOreData(index);
                        oreData.setOreAmount(amount);
                        if (args.length >= 4)
                        {
                            String oreID = args[3];
                            IForgeRegistry<Item> registry = GameRegistry.findRegistry(Item.class);
                            if (!registry.containsKey(new ResourceLocation(oreID)))
                            {
                                throw new WrongUsageException("txt.f0r.command.set.invalidID");
                            }

                            oreData.setOreItem(registry.getValue(new ResourceLocation(oreID)));
                        }

                        if (args.length >= 5)
                        {
                            try
                            {
                                oreData.setOreMeta(Short.parseShort(args[4]));
                            }
                            catch (NumberFormatException ex)
                            {
                                throw new WrongUsageException("txt.f0r.command.set.invalidMeta");
                            }
                        }

                        if (args.length >= 6)
                        {
                            try
                            {
                                oreData.setTierReq(Short.parseShort(args[5]));
                            }
                            catch (NumberFormatException ex)
                            {
                                throw new WrongUsageException("txt.f0r.command.set.invalidTier");
                            }
                        }
                    }
                    else
                    {
                        if (arg.equalsIgnoreCase("remove"))
                        {
                            IF0RWorld world = IF0RWorld.of(sender.getEntityWorld());
                            IChunkData data = world.getLoadedChunkData(new ChunkPos(sender.getPosition()));
                            if (data == null)
                            {
                                throw new WrongUsageException("txt.f0r.command.remove.nullData");
                            }

                            if (args.length == 1)
                            {
                                throw new WrongUsageException("txt.f0r.command.remove.notEnoughArgs");
                            }

                            int index;
                            try
                            {
                                index = Integer.parseInt(args[1]);
                            }
                            catch (NumberFormatException ex)
                            {
                                throw new WrongUsageException("txt.f0r.command.remove.invalidParams");
                            }

                            if (data.getSize() == 0)
                            {
                                throw new WrongUsageException("txt.f0r.command.remove.noData");
                            }

                            if (index >= data.getSize() || index < 0)
                            {
                                throw new WrongUsageException("txt.f0r.command.remove.indexTooBig", index - 1);
                            }

                            data.removeOreData(data.getOreData(index));
                        }
                        else
                        {
                            if (arg.equalsIgnoreCase("add"))
                            {
                                if (args.length < 5)
                                {
                                    throw new WrongUsageException("txt.f0r.command.add.notEnoughArgs");
                                }

                                IF0RWorld world = IF0RWorld.of(sender.getEntityWorld());
                                IChunkData data = world.getLoadedChunkData(new ChunkPos(sender.getPosition()));
                                if (data == null)
                                {
                                    throw new WrongUsageException("txt.f0r.command.add.nullData");
                                }

                                int tier;
                                int meta;
                                int amount;
                                Item item;
                                IForgeRegistry<Item> registry = GameRegistry.findRegistry(Item.class);
                                if (!registry.containsKey(new ResourceLocation(args[1])))
                                {
                                    throw new WrongUsageException("txt.f0r.command.add.invalidID");
                                }

                                item = registry.getValue(new ResourceLocation(args[1]));
                                try
                                {
                                    meta = Integer.parseInt(args[2]);
                                }
                                catch (NumberFormatException nfe)
                                {
                                    throw new WrongUsageException("txt.f0r.command.add.invalidMeta");
                                }

                                try
                                {
                                    tier = Integer.parseInt(args[3]);
                                }
                                catch (NumberFormatException nfe)
                                {
                                    throw new WrongUsageException("txt.f0r.command.add.invalidTier");
                                }

                                try
                                {
                                    amount = Integer.parseInt(args[4]);
                                }
                                catch (NumberFormatException nfe)
                                {
                                    throw new WrongUsageException("txt.f0r.command.add.invalidAmount");
                                }

                                OreData oreData = new OreData();
                                oreData.oreBlock = item;
                                oreData.oreMeta = (short) meta;
                                oreData.amount = amount;
                                oreData.tierReq = (byte) tier;
                                data.addOreData(oreData);
                            }
                        }
                    }
                }
            }
        }
    }
}
