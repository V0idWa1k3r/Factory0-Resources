package v0id.f0resources.client;

import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.OreDictionary;
import v0id.api.f0resources.data.F0RRegistryNames;
import v0id.f0resources.F0Resources;

import java.util.Arrays;

@Mod.EventBusSubscriber(modid = F0RRegistryNames.MODID, value = { Side.CLIENT })
public class F0RClientHandler
{
    @SubscribeEvent
    public static void onTooltipAdded(ItemTooltipEvent event)
    {
        if (event.getFlags().isAdvanced() && F0Resources.isDevEnvironment)
        {
            Arrays.stream(OreDictionary.getOreIDs(event.getItemStack())).mapToObj(OreDictionary::getOreName).forEach(e -> event.getToolTip().add(e));
        }
    }
}
