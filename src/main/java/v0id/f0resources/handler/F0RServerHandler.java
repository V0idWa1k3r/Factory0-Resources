package v0id.f0resources.handler;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import v0id.api.f0resources.data.F0RRegistryNames;
import v0id.api.f0resources.world.F0RWorldCapability;
import v0id.api.f0resources.world.IChunkData;
import v0id.api.f0resources.world.IF0RWorld;
import v0id.f0resources.chunk.ChunkData;
import v0id.f0resources.chunk.ChunkOreGenerator;
import v0id.f0resources.world.F0RWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = F0RRegistryNames.MODID)
public class F0RServerHandler
{
    @SubscribeEvent
    public static void attachCapsEvent(AttachCapabilitiesEvent<World> event)
    {
        event.addCapability(F0RWorldCapability.KEY, new ICapabilityProvider()
        {
            private F0RWorld data = new F0RWorld();

            @Override
            public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
            {
                return capability == F0RWorldCapability.cap;
            }

            @Nullable
            @Override
            public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
            {
                return capability == F0RWorldCapability.cap ? F0RWorldCapability.cap.cast(this.data) : null;
            }
        });
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkDataEvent.Load event)
    {
        if (event.getData().hasKey("f0-resources"))
        {
            NBTTagCompound tag = event.getData().getCompoundTag("f0-resources");
            IF0RWorld world = IF0RWorld.of(event.getWorld());
            IChunkData data = world.getLoadedChunkData(event.getChunk().getPos());
            if (data == null)
            {
                world.loadChunkData(event.getChunk().getPos(), new ChunkData());
            }

            data = world.getLoadedChunkData(event.getChunk().getPos());
            data.deserializeNBT(tag);
        }
        else
        {
            IF0RWorld world = IF0RWorld.of(event.getWorld());
            IChunkData data = world.getLoadedChunkData(event.getChunk().getPos());
            if (data == null)
            {
                data = new ChunkData();
                world.loadChunkData(event.getChunk().getPos(), data);
            }

            ChunkOreGenerator.generateData(event.getWorld(), event.getChunk().getPos(), (ChunkData) data);
        }
    }

    @SubscribeEvent
    public static void onChunkSave(ChunkDataEvent.Save event)
    {
        IF0RWorld world = IF0RWorld.of(event.getWorld());
        if (world.getLoadedChunkData(event.getChunk().getPos()) != null)
        {
            event.getData().setTag("f0-resources", world.getLoadedChunkData(event.getChunk().getPos()).serializeNBT());
        }
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event)
    {
        IF0RWorld world = IF0RWorld.of(event.getWorld());
        world.unloadChunkData(event.getChunk().getPos());
    }

    @SubscribeEvent
    public static void onChunkGenerated(PopulateChunkEvent.Pre event)
    {
        IF0RWorld world = IF0RWorld.of(event.getWorld());
        ChunkData data = new ChunkData();
        ChunkPos pos = new ChunkPos(event.getChunkX(), event.getChunkZ());
        if (world.getLoadedChunkData(pos) == null)
        {
            ChunkOreGenerator.generateData(event.getWorld(), pos, data);
            world.loadChunkData(pos, data);
        }
    }
}
