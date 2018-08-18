package v0id.api.f0resources.world;


import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Iterator;
import java.util.Spliterator;

public interface IChunkData extends Iterable<IOreData>, INBTSerializable<NBTTagCompound>
{
    void addOreData(IOreData data);

    void removeOreData(IOreData data);

    int getSize();

    int getFluidLength();

    IOreData getOreData(int i);

    IFluidData getFluidData(int i);

    void addFluidData(IFluidData data);

    void removeFluidData(IFluidData data);

    Spliterator<IOreData> spliterator();

    Spliterator<IFluidData> fluidSpliterator();

    Iterator<IFluidData> fluidIterator();
}
