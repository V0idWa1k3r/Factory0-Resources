package v0id.api.f0resources.world;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import v0id.api.f0resources.data.F0RRegistryNames;

import javax.annotation.Nullable;

public class F0RWorldCapability
{
    @CapabilityInject(IF0RWorld.class)
    public static final Capability<IF0RWorld> cap = null;
    public static final ResourceLocation KEY = new ResourceLocation(F0RRegistryNames.MODID, "world_data_capability");
    public static Class<?> providerClazz;

    static
    {
        try
        {
            providerClazz = Class.forName("v0id.f0resources.world.F0RWorld");
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IF0RWorld.class, new Capability.IStorage<IF0RWorld>()
        {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<IF0RWorld> capability, IF0RWorld instance, EnumFacing side)
            {
                return null;
            }

            @Override
            public void readNBT(Capability<IF0RWorld> capability, IF0RWorld instance, EnumFacing side, NBTBase nbt)
            {

            }
        }, () -> (IF0RWorld) providerClazz.newInstance());
    }
}
