package v0id.f0resources.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import v0id.api.f0resources.data.F0RRegistryNames;

@Config(modid = F0RRegistryNames.MODID)
public class F0RConfig
{
    @Config.Comment("The noise position is offset by chunkPosition / this value. The higher the value the more stretched ore veins become. The lower the value - the more localized the ore veins become.")
    public static double noiseChunkDivider = 32;

    @Config.Comment("The amount of energy the drill stores in it.")
    @Config.RangeInt(min = 0)
    public static int drillEnergy = 1000000;

    @Config.Comment("The amount of energy the drill consumes per tick.")
    @Config.RangeInt(min = 0)
    public static int drillEnergyConsumption = 1024;

    @Config.Comment("The block the drill must be placed on to work")
    public static String requiredBlock = "minecraft:stone";

    @Config.Comment("The amount of progress the drill has to accumulate before it produces an ore")
    public static float drillRequiredProgress = 20F;

    @Config.Comment("The stacksize of the ore item the drill produces.")
    @Config.RangeInt(min = 1)
    public static int oreResultSize = 1;

    @Config.Comment("The value by which the drill head gets damaged.")
    @Config.RangeInt(min = 0)
    public static int drillHeadDamage = 1;

    @Config.Comment("Whether the drill should reduce the amount of ore in the chunk when mining.")
    public static boolean reduceOreInTheChunk = true;

    @Config.Comment("The amount by which the ore is reduced when the drill mines it")
    @Config.RangeInt(min = 0)
    public static int oreReducedBy = 1;

    @Config.Comment("Should the prospector's pick display the prospecting message in the chat or in the action bar?")
    public static boolean displayProspectorMessageInChat = false;

    @Config.Comment("Should the multiplier of 0 to the progress be allowed in the ores.json? Set this to true if you are configuring ore entries to have a 0 progress multiplier manually.")
    public static boolean allowZeroOreProgressMultiplier = false;

    @Config.Comment("The maximum amount of FE energy the scanner is able to hold")
    public static int scannerMaxEnergy = 100000;

    @Config.Comment("How much energy the scanner uses per scan")
    public static int scannerEnergyCost = 1000;

    @Mod.EventBusSubscriber(modid = F0RRegistryNames.MODID)
    public static class ConfigHandler
    {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
        {
            if (event.getModID().equalsIgnoreCase(F0RRegistryNames.MODID))
            {
                ConfigManager.sync(F0RRegistryNames.MODID, Config.Type.INSTANCE);
            }
        }
    }
}
