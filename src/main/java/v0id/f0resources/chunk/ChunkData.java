package v0id.f0resources.chunk;

import com.google.common.collect.Lists;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import v0id.api.f0resources.world.IChunkData;
import v0id.api.f0resources.world.IOreData;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.StreamSupport;

public class ChunkData implements IChunkData
{
    public List<OreData> oreData = Lists.newArrayList();

    @Override
    public void addOreData(IOreData data)
    {
        if (data instanceof OreData)
        {
            oreData.add((OreData) data);
        }
        else
        {
            throw new UnsupportedOperationException("Unknown ore data type passed, please use OreData!");
        }
    }

    @Override
    public void removeOreData(IOreData data)
    {
        if (data instanceof OreData)
        {
            oreData.remove(data);
        }
        else
        {
            throw new UnsupportedOperationException("Unknown ore data type passed, please use OreData!");
        }
    }

    @Override
    public int getSize()
    {
        return this.oreData.size();
    }

    @Override
    public IOreData getOreData(int i)
    {
        return this.oreData.get(i);
    }

    @Override
    public Spliterator<IOreData> spliterator()
    {
        return this.oreData.stream().map(e -> (IOreData)e).spliterator();
    }

    @Override
    public Iterator<IOreData> iterator()
    {
        return this.oreData.stream().map(e -> (IOreData)e).iterator();
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound ret = new NBTTagCompound();
        NBTTagList lst = new NBTTagList();
        this.oreData.stream().map(OreData::serializeNBT).forEach(lst::appendTag);
        ret.setTag("data", lst);
        return ret;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        NBTTagList lst = nbt.getTagList("data", Constants.NBT.TAG_COMPOUND);
        this.oreData.clear();
        StreamSupport.stream(lst.spliterator(), false).map(e -> (NBTTagCompound) e).forEach(e ->
        {
            OreData data = new OreData();
            data.deserializeNBT(e);
            this.oreData.add(data);
        });
    }
}
