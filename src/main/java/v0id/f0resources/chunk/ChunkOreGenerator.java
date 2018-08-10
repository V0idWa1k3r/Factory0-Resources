package v0id.f0resources.chunk;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import v0id.api.f0resources.noise.SimplexNoise2D;
import v0id.f0resources.config.F0RConfig;
import v0id.f0resources.config.OreEntry;

import java.util.Random;

public class ChunkOreGenerator
{
    public static final Random RANDOM = new Random();
    private static final IForgeRegistry<Item> ITEM_REGISTRY = GameRegistry.findRegistry(Item.class);

    public static void generateData(World world, ChunkPos pos, ChunkData data)
    {
        generateData(world.getSeed(), world.provider.getDimension(), pos, data);
    }

    public static void generateData(long seed, int dimension, ChunkPos pos, ChunkData data)
    {
        for (OreEntry oreEntry : OreEntry.allEntries)
        {
            if (oreEntry.valid && oreEntry.canGenerateIn(dimension))
            {
                RANDOM.setSeed(seed | oreEntry.seed);
                double offsetX = RANDOM.nextDouble();
                double offsetZ = RANDOM.nextDouble();
                offsetX += (double) pos.x / F0RConfig.noiseChunkDivider * oreEntry.stretchX;
                offsetZ += (double) pos.z / F0RConfig.noiseChunkDivider * oreEntry.stretchZ;
                double noise = SimplexNoise2D.noise(offsetX, offsetZ) * oreEntry.valueModifier;
                if (noise >= oreEntry.oreMinimum)
                {
                    OreData oreData = new OreData();
                    oreData.oreBlock = ITEM_REGISTRY.getValue(new ResourceLocation(oreEntry.oreID));
                    oreData.oreMeta = (short) oreEntry.oreMeta;
                    oreData.tierReq = (byte) oreEntry.tierReq;
                    oreData.amount = (int) (noise * oreEntry.oreMaximum);
                    data.addOreData(oreData);
                }
            }
        }
    }
}
