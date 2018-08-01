package v0id.f0resources.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import v0id.f0resources.F0Resources;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.StreamSupport;

public class DrillMaterialEntry
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static DrillMaterialEntry[] allEntries = new DrillMaterialEntry[0];
    public static DrillMaterialEntry[] defaults = new DrillMaterialEntry[]{
            // Vanilla
            new DrillMaterialEntry("f0r.wood", true, 60, 0x6b511f, 0, 0.5F),
            new DrillMaterialEntry("f0r.stone", true, 132, 0x7f7f7f, 1, 0.75F),
            new DrillMaterialEntry("f0r.iron", true, 251, 0xd8d8d8, 2, 1F),
            new DrillMaterialEntry("f0r.gold", true, 33, 0xeaee57, 1, 5F),
            new DrillMaterialEntry("f0r.diamond", true, 1562, 0x33ebcb, 3, 1.5F),

            // Thermal Expansion
            new DrillMaterialEntry("f0r.copper", true, 176, 0xffae58, 1, 0.75F),
            new DrillMaterialEntry("f0r.tin", true, 150, 0xc7e6f2, 1, 0.6F),
            new DrillMaterialEntry("f0r.silver", true, 75, 0xcfedf4, 1, 2F),
            new DrillMaterialEntry("f0r.lead", true, 100, 0x3b466d, 1, 0.4F),
            new DrillMaterialEntry("f0r.aluminum", true, 225, 0xd4d4df, 1, 1F),
            new DrillMaterialEntry("f0r.nickel", true, 300, 0xefe9b5, 2, 0.9F),
            new DrillMaterialEntry("f0r.platinum", true, 1400, 0xc6f0ff, 4, 4F),
            new DrillMaterialEntry("f0r.steel", true, 400, 0x959595, 2, 1.1F),
            new DrillMaterialEntry("f0r.electrum", true, 100, 0xe8e27c, 0, 1.5F),
            new DrillMaterialEntry("f0r.invar", true, 425, 0xd7dbd9, 2, 1F),
            new DrillMaterialEntry("f0r.bronze", true, 325, 0xef9a36, 2, 1F),
            new DrillMaterialEntry("f0r.constantan", true, 275, 0xebcc91, 2, 1F),
            new DrillMaterialEntry("f0r.enderium", true, 1841, 0x0f5f5f, 4, 2F),

            // Ender IO
            new DrillMaterialEntry("f0r.electricalSteel", true, 325, 0xd8d8d8, 2, 1F),
            new DrillMaterialEntry("f0r.darkSteel", true, 1241, 0x5a5a5a, 3, 1.5F),
            new DrillMaterialEntry("f0r.soularium", true, 456, 0x756048, 1, 1F),
            new DrillMaterialEntry("f0r.vibrant", true, 225, 0xfae373, 1, 8F),
    };

    public String name;
    public boolean isUnlocalized;
    public int durability;
    public int color;
    public int tier;
    public float speed;

    public DrillMaterialEntry(String name, boolean isUnlocalized, int durability, int color, int tier, float speed)
    {
        this.name = name;
        this.isUnlocalized = isUnlocalized;
        this.durability = durability;
        this.color = color;
        this.tier = tier;
        this.speed = speed;
    }

    public static void parse()
    {
        try
        {
            File path = new File(new File(Loader.instance().getConfigDir(), "F0Resources"), "drills.json");
            if (path.exists())
            {
                String contents = FileUtils.readFileToString(path, StandardCharsets.UTF_8);
                JsonArray array = new JsonParser().parse(contents).getAsJsonArray();
                allEntries = new DrillMaterialEntry[array.size()];
                AtomicInteger aint = new AtomicInteger(0);
                StreamSupport.stream(array.spliterator(), false).map(e -> e.getAsJsonObject()).forEach(o -> allEntries[aint.getAndIncrement()] = GSON.fromJson(o, DrillMaterialEntry.class));
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
            F0Resources.modLogger.fatal("Drill configs could not be loaded!", ex);
            FMLCommonHandler.instance().raiseException(ex, "Drill Configs could not be loaded!", true);
        }
    }
}
