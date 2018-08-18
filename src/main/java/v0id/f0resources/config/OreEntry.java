package v0id.f0resources.config;

import com.google.gson.*;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import v0id.f0resources.F0Resources;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.StreamSupport;

public class OreEntry
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static OreEntry[] allEntries = new OreEntry[0];
    public static OreEntry[] defaults = new OreEntry[]{
        // Vanilla
        new OreEntry(1, 1, 23652533L, 1, 6000, 0.2, "minecraft:coal_ore", 0, new int[]{ 1, -1 }, false, 0),
        new OreEntry(2, 2, 14662363532L, 1, 3000, 0.3, "minecraft:iron_ore", 0, new int[]{ 1, -1 }, false, 1),
        new OreEntry(3, 3, 346747643L, 1, 1500, 0.4, "minecraft:gold_ore", 0, new int[]{ 1, -1 }, false, 2),
        new OreEntry(2, 2, 235352311L, 1, 2600, 0.2, "minecraft:redstone_ore", 0, new int[]{ 1, -1 }, false, 2),
        new OreEntry(2.5, 2.5, 1207510918L, 1, 1400, 0.3, "minecraft:lapis_ore", 0, new int[]{ 1, -1 }, false, 1),
        new OreEntry(5, 5, 23526523525L, 0.9, 1000, 0.5, "minecraft:diamond_ore", 0, new int[]{ 1, -1 }, false, 2),
        new OreEntry(4, 4, 3642553252L, 1, 800, 0.5, "minecraft:emerald_ore", 0, new int[]{ 1, -1 }, false, 2),
        new OreEntry(1, 1, 3205391L, 1, 5000, 0.2, "minecraft:quartz_ore", 0, new int[]{ -1 }, true, 1),

        // Thermal Foundation
        new OreEntry(2, 2, 925977991248L, 1, 4000, 0.3, "thermalfoundation:ore", 0, new int[]{ 1, -1 }, false, 1),
        new OreEntry(2, 2, 321313414L, 1, 4500, 0.3, "thermalfoundation:ore", 1, new int[]{ 1, -1 }, false, 1),
        new OreEntry(3, 3, 31231214L, 1, 2000, 0.4, "thermalfoundation:ore", 2, new int[]{ 1, -1 }, false, 2),
        new OreEntry(3, 3, 31231214L, 1, 3500, 0.3, "thermalfoundation:ore", 3, new int[]{ 1, -1 }, false, 2),
        new OreEntry(2, 2, 3124525515L, 1, 2000, 0.4, "thermalfoundation:ore", 5, new int[]{ 1, -1 }, false, 2),
        new OreEntry(5, 5, 124124151L, 1, 1000, 0.5, "thermalfoundation:ore", 6, new int[]{ 1, -1 }, false, 3)
    };

    public double stretchX;
    public double stretchZ;
    public long seed;
    public double valueModifier;
    public int oreMaximum;
    public double oreMinimum;
    public String oreID;
    public int oreMeta;
    public int[] dimensionBlacklist;
    public boolean isBlacklistWhitelist;
    public int tierReq;
    public float progressMultiplier = 1F;

    public transient boolean valid = true;

    public OreEntry(double stretchX, double stretchZ, long seed, double valueModifier, int oreMaximum, double oreMinimum, String oreID, int oreMeta, int[] dimensionBlacklist, boolean isBlacklistWhitelist, int tierReq)
    {
        this.stretchX = stretchX;
        this.stretchZ = stretchZ;
        this.seed = seed;
        this.valueModifier = valueModifier;
        this.oreMaximum = oreMaximum;
        this.oreMinimum = oreMinimum;
        this.oreID = oreID;
        this.oreMeta = oreMeta;
        this.dimensionBlacklist = dimensionBlacklist;
        this.isBlacklistWhitelist = isBlacklistWhitelist;
        this.tierReq = tierReq;
    }

    public static OreEntry findByItem(Item item, int metadata)
    {
        return Arrays.stream(OreEntry.allEntries).filter(e -> e.oreID.equalsIgnoreCase(item.getRegistryName().toString()) && e.oreMeta == metadata).findFirst().orElse(null);
    }

    public void validate()
    {
        this.valid = GameRegistry.findRegistry(Item.class).containsKey(new ResourceLocation(this.oreID));
        if (!F0RConfig.allowZeroOreProgressMultiplier && this.progressMultiplier <= 0.0001F)
        {
            this.progressMultiplier = 1F;
        }
    }

    public boolean canGenerateIn(int dimension)
    {
        return this.isBlacklistWhitelist == ArrayUtils.contains(this.dimensionBlacklist, dimension);
    }

    public static void parse()
    {
        try
        {
            File path = new File(new File(Loader.instance().getConfigDir(), "F0Resources"), "ores.json");
            if (path.exists())
            {
                String contents = FileUtils.readFileToString(path, StandardCharsets.UTF_8);
                JsonArray array = new JsonParser().parse(contents).getAsJsonArray();
                allEntries = new OreEntry[array.size()];
                AtomicInteger aint = new AtomicInteger(0);
                StreamSupport.stream(array.spliterator(), false).map(JsonElement::getAsJsonObject).forEach(o -> allEntries[aint.getAndIncrement()] = GSON.fromJson(o, OreEntry.class));
            }
            else
            {
                path.getParentFile().mkdirs();
                try (FileOutputStream fos = new FileOutputStream(path))
                {
                    IOUtils.write(GSON.toJson(defaults), fos, StandardCharsets.UTF_8);
                }

                allEntries = defaults;
            }
        }
        catch (Exception ex)
        {
            F0Resources.modLogger.fatal("Ore configs could not be loaded!", ex);
            FMLCommonHandler.instance().raiseException(ex, "Ore Configs could not be loaded!", true);
        }
    }
}
