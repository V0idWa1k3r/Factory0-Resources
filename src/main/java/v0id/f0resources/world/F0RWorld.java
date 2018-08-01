package v0id.f0resources.world;

import com.google.common.collect.Maps;
import net.minecraft.util.math.ChunkPos;
import v0id.api.f0resources.world.IChunkData;
import v0id.api.f0resources.world.IF0RWorld;
import v0id.f0resources.chunk.ChunkData;

import java.util.Map;

public class F0RWorld implements IF0RWorld
{
    public Map<ChunkPos, IChunkData> chunkDataMap = Maps.newHashMap();

    @Override
    public Map<ChunkPos, IChunkData> getAllLoadedChunkData()
    {
        return this.chunkDataMap;
    }

    @Override
    public IChunkData getLoadedChunkData(ChunkPos pos)
    {
        return this.chunkDataMap.getOrDefault(pos, null);
    }

    @Override
    public void setLoadedChunkData(ChunkPos pos, IChunkData data)
    {
        if (this.chunkDataMap.containsKey(pos))
        {
            this.loadChunkData(pos, data);
        }
    }

    @Override
    public void unloadChunkData(ChunkPos pos)
    {
        chunkDataMap.remove(pos);
    }

    @Override
    public void loadChunkData(ChunkPos pos, IChunkData data)
    {
        if (data instanceof ChunkData)
        {
            this.chunkDataMap.put(pos, data);
        }
        else
        {
            throw new UnsupportedOperationException("Unknown chunk data passed! Please use instances of ChunkData!");
        }
    }

    @Override
    public void unloadAllChunkData()
    {
        this.chunkDataMap.clear();
    }
}
