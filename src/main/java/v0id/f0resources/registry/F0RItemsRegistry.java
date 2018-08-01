package v0id.f0resources.registry;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import v0id.api.f0resources.data.F0RRegistryNames;
import v0id.f0resources.config.DrillMaterialEntry;
import v0id.f0resources.item.ItemDrillHead;
import v0id.f0resources.item.ItemProspectorsPick;

import java.util.Arrays;

@Mod.EventBusSubscriber(modid = F0RRegistryNames.MODID)
public class F0RItemsRegistry
{
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().registerAll(
                new ItemProspectorsPick(false),
                new ItemProspectorsPick(true)
        );

        Arrays.stream(DrillMaterialEntry.allEntries).forEach(e -> event.getRegistry().register(new ItemDrillHead(e)));
    }
}
