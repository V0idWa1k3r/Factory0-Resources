package v0id.f0resources.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class ServerProxy implements IProxy
{
    @Override
    public IThreadListener getContextListener()
    {
        return FMLCommonHandler.instance().getMinecraftServerInstance();
    }

    @Override
    public World getClientWorld()
    {
        return null;
    }

    @Override
    public EntityPlayer getClientPlayer()
    {
        return null;
    }

    @Override
    public int getViewDistance()
    {
        MinecraftServer server = DimensionManager.getWorld(0).getMinecraftServer();

        //Should always be true
        if (server instanceof DedicatedServer)
        {
            return ((DedicatedServer) server).getIntProperty("view-distance", 10);
        }

        return 10;
    }

    @Override
    public void addToast(ItemStack icon, String langKey)
    {

    }
}
