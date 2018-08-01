package v0id.f0resources.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import v0id.f0resources.client.ClientRegistry;

public class ClientProxy implements IProxy
{
    @Override
    public IThreadListener getContextListener()
    {
        return Minecraft.getMinecraft();
    }

    @Override
    public World getClientWorld()
    {
        return Minecraft.getMinecraft().world;
    }

    @Override
    public EntityPlayer getClientPlayer()
    {
        return Minecraft.getMinecraft().player;
    }

    @Override
    public int getViewDistance()
    {
        return Minecraft.getMinecraft().gameSettings.renderDistanceChunks;
    }

    @Override
    public void init(FMLInitializationEvent event)
    {
        ClientRegistry.registerColours();
    }
}
