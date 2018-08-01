package v0id.api.f0resources.world;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.Map;

public interface IF0RWorld
{
    Map<ChunkPos, IChunkData> getAllLoadedChunkData();

    IChunkData getLoadedChunkData(ChunkPos pos);

    void setLoadedChunkData(ChunkPos pos, IChunkData data);

    void unloadChunkData(ChunkPos pos);

    void loadChunkData(ChunkPos pos, IChunkData data);

    void unloadAllChunkData();

    static IF0RWorld of(World w)
    {
        return w.getCapability(F0RWorldCapability.cap, null);
    }
}
