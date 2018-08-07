package v0id.api.f0resources.data;

import com.google.common.collect.Maps;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

public class F0RRegistryNames
{
    public static final String MODID                                                            = "f0-resources";

    public static final String
        drillComponent                                                                          = "drill_component",
        drillPart                                                                               = "drill_part",
        burnerDrillComponent                                                                    = "burner_drill_component",
        burnerDrillPart                                                                         = "burner_drill_part";

    public static final String
        prospectorsPick                                                                         = "item_prospectors_pick",
        advancedProspectorsPick                                                                 = "item_advanced_prospectors_pick",
        scanner                                                                                 = "item_scanner",
        advancedScanner                                                                         = "item_advanced_scanner";

    private static final Map<String, ResourceLocation> cache = Maps.newHashMap();

    public static ResourceLocation asLocation(String name)
    {
        return asLocation(name, false);
    }

    public static ResourceLocation asLocation(String name, boolean doCache)
    {
        if (doCache)
        {
            if (!cache.containsKey(name))
            {
                cache.put(name, new ResourceLocation(MODID, name));
            }

            return cache.get(name);
        }
        else
        {
            return new ResourceLocation(MODID, name);
        }
    }
}
