package v0id.f0resources.util;

import net.minecraft.util.WeightedRandom;
import v0id.api.f0resources.world.IOreData;

public class OreItem extends WeightedRandom.Item
{
    public final IOreData data;

    public OreItem(IOreData data)
    {
        super(data.getOreAmount());
        this.data = data;
    }
}
