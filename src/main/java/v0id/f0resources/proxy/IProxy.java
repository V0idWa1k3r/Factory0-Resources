package v0id.f0resources.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import v0id.api.f0resources.util.ILifecycleListener;

public interface IProxy extends ILifecycleListener
{
    IThreadListener getContextListener();

    World getClientWorld();

    EntityPlayer getClientPlayer();

    int getViewDistance();

    void addToast(ItemStack icon, String langKey);

    void storeSeed(long seed);
}
