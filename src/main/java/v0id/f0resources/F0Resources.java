package v0id.f0resources;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.CoreModManager;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import v0id.api.f0resources.capability.IFuelHandler;
import v0id.api.f0resources.data.F0RRegistryNames;
import v0id.api.f0resources.world.F0RWorldCapability;
import v0id.f0resources.capability.FuelHandler;
import v0id.f0resources.config.DrillMaterialEntry;
import v0id.f0resources.config.OreEntry;
import v0id.f0resources.handler.GuiHandler;
import v0id.f0resources.network.F0RNetwork;
import v0id.f0resources.proxy.IProxy;
import v0id.f0resources.registry.F0ROreDictRegistry;
import v0id.f0resources.server.CommandF0R;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

@Mod(modid = F0RRegistryNames.MODID, useMetadata = true, certificateFingerprint = "43787005475f132f5fc1e851b9247fda75ed5d52")
public class F0Resources
{
    static
    {
        CapabilityManager.INSTANCE.register(IFuelHandler.class, new Capability.IStorage<IFuelHandler>()
        {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<IFuelHandler> capability, IFuelHandler instance, EnumFacing side)
            {
                return instance.serializeNBT();
            }

            @Override
            public void readNBT(Capability<IFuelHandler> capability, IFuelHandler instance, EnumFacing side, NBTBase nbt)
            {
                instance.deserializeNBT((NBTTagCompound) nbt);
            }
        }, FuelHandler::new);
    }

    @Mod.Instance(F0RRegistryNames.MODID)
    public static F0Resources instance;

    public static Logger modLogger = LogManager.getLogger("F0R[Main]");

    @SidedProxy(clientSide = "v0id.f0resources.proxy.ClientProxy", serverSide = "v0id.f0resources.proxy.ServerProxy")
    public static IProxy proxy;

    public static boolean isDevEnvironment;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        setDevEnvironment();
        OreEntry.parse();
        DrillMaterialEntry.parse();
        F0RWorldCapability.register();
        F0RNetwork.loadClass();
        proxy.preInit(event);
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        Arrays.stream(OreEntry.allEntries).forEach(OreEntry::validate);
        F0ROreDictRegistry.register();
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public static void serverStarting(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new CommandF0R());
    }

    @Mod.EventHandler
    public static void fingerprintViolated(FMLFingerprintViolationEvent event)
    {
        if (event.isDirectory())
        {
            modLogger.warn("Factory0-Resources fingerprint doesn't match but we are in a dev environment so that's okay.");
        }
        else
        {
            modLogger.error("Factory0-Resources fingerprint doesn't match! Expected {}, got {}!", event.getExpectedFingerprint(), event.getFingerprints().stream().collect(Collectors.joining(" , ")));
        }
    }

    private void setDevEnvironment()
    {
        try
        {
            Class<CoreModManager> clazz = CoreModManager.class;
            Field f = clazz.getDeclaredField("deobfuscatedEnvironment");
            boolean accessibilityFlag = f.isAccessible();
            f.setAccessible(true);
            isDevEnvironment = f.getBoolean(null);
            f.setAccessible(accessibilityFlag);
            if (isDevEnvironment)
            {
                modLogger.log(Level.INFO, "F0Resources has detected dev environment! Additional debug features enabled!");
            }
            else
            {
                modLogger.log(Level.INFO, "F0Resources has detected normal minecraft environment. No debug features enabled.");
            }
        }
        catch (Exception ex)
        {
            modLogger.log(Level.INFO, "F0Resources was unable to determine the environment it is located in! Assuming normal minecraft instance.");
        }
    }
}
