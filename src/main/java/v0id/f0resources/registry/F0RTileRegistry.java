package v0id.f0resources.registry;

import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import v0id.api.f0resources.data.F0RRegistryNames;
import v0id.f0resources.tile.TileBurnerDrill;
import v0id.f0resources.tile.TileDrill;
import v0id.f0resources.tile.TileFluidPump;

@Mod.EventBusSubscriber(modid = F0RRegistryNames.MODID)
public class F0RTileRegistry
{
    @SubscribeEvent
    public static void onBlocksRegistry(RegistryEvent.Register<Block> event)
    {
        GameRegistry.registerTileEntity(TileDrill.class, F0RRegistryNames.asLocation("drill"));
        GameRegistry.registerTileEntity(TileBurnerDrill.class, F0RRegistryNames.asLocation("burner_drill"));
        GameRegistry.registerTileEntity(TileFluidPump.class, F0RRegistryNames.asLocation("fluid_pump"));
    }
}
