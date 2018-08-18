package v0id.f0resources.config;

import com.google.gson.*;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
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

public class FluidEntry
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static FluidEntry[] allEntries = new FluidEntry[0];
    public static FluidEntry[] defaults = new FluidEntry[]{
        new FluidEntry(1, 1, 234174891L, 1.0, 10000000, 0.1, "water", new int[]{ 1, -1, }, false, 50, false, 1.0F, 0.2F),
        new FluidEntry(3, 3, 14151412L, 1.0, 1000000, 0.3, "lava", new int[]{ 1, -1 }, false, 10, false, 1.0F, 0.2F),
        new FluidEntry(0.5, 0.5, 234141, 1.0, 100000000, 0.1, "lava", new int[]{ -1 }, true, 10, false, 1.0F, 0.2F),
        new FluidEntry(5, 5, 454141L, 1.0, 5000000, 0.1, "oil", new int[]{ 1, -1 }, false, 20, true, 2.0F, 0.2F),
    };

    public double stretchX;
    public double stretchZ;
    public long seed;
    public double valueModifier;
    public long fluidMaximum;
    public double fluidMinimum;
    public String fluidID;
    public int[] dimensionBlacklist;
    public boolean isBlacklistWhitelist;
    public int fluidDrainRate;
    public boolean isDrainRateBasedOnFluidAmount;
    public float drainRateMax;
    public float drainRateMin;

    public transient boolean valid;

    public FluidEntry(double stretchX, double stretchZ, long seed, double valueModifier, long fluidMaximum, double fluidMinimum, String fluidID, int[] dimensionBlacklist, boolean isBlacklistWhitelist, int fluidDrainRate, boolean isDrainRateBasedOnFluidAmount, float drainRateMax, float drainRateMin)
    {
        this.stretchX = stretchX;
        this.stretchZ = stretchZ;
        this.seed = seed;
        this.valueModifier = valueModifier;
        this.fluidMaximum = fluidMaximum;
        this.fluidMinimum = fluidMinimum;
        this.fluidID = fluidID;
        this.dimensionBlacklist = dimensionBlacklist;
        this.isBlacklistWhitelist = isBlacklistWhitelist;
        this.fluidDrainRate = fluidDrainRate;
        this.isDrainRateBasedOnFluidAmount = isDrainRateBasedOnFluidAmount;
        this.drainRateMax = drainRateMax;
        this.drainRateMin = drainRateMin;
    }

    public static FluidEntry findByFluid(Fluid fluid)
    {
        return Arrays.stream(FluidEntry.allEntries).filter(e -> e.fluidID.equalsIgnoreCase(fluid.getName())).findFirst().orElse(null);
    }

    public void validate()
    {
        this.valid = FluidRegistry.getFluid(this.fluidID) != null;
    }

    public boolean canGenerateIn(int dimension)
    {
        return this.isBlacklistWhitelist == ArrayUtils.contains(this.dimensionBlacklist, dimension);
    }

    public static void parse()
    {
        try
        {
            File path = new File(new File(Loader.instance().getConfigDir(), "F0Resources"), "fluids.json");
            if (path.exists())
            {
                String contents = FileUtils.readFileToString(path, StandardCharsets.UTF_8);
                JsonArray array = new JsonParser().parse(contents).getAsJsonArray();
                allEntries = new FluidEntry[array.size()];
                AtomicInteger aint = new AtomicInteger(0);
                StreamSupport.stream(array.spliterator(), false).map(JsonElement::getAsJsonObject).forEach(o -> allEntries[aint.getAndIncrement()] = GSON.fromJson(o, FluidEntry.class));
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
            F0Resources.modLogger.fatal("Fluid configs could not be loaded!", ex);
            FMLCommonHandler.instance().raiseException(ex, "Fluid Configs could not be loaded!", true);
        }
    }
}
