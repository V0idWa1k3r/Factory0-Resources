package v0id.api.f0resources.world;


import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Spliterator;

public interface IChunkData extends Iterable<IOreData>, INBTSerializable<NBTTagCompound>
{
    void addOreData(IOreData data);

    void removeOreData(IOreData data);

    int getSize();

    IOreData getOreData(int i);

    Spliterator<IOreData> spliterator();
}
