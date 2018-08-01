package v0id.f0resources.tile;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.IForgeRegistry;
import v0id.api.f0resources.world.IChunkData;
import v0id.api.f0resources.world.IF0RWorld;
import v0id.api.f0resources.world.IOreData;
import v0id.f0resources.F0Resources;
import v0id.f0resources.config.DrillMaterialEntry;
import v0id.f0resources.config.F0RConfig;
import v0id.f0resources.item.ItemDrillHead;
import v0id.f0resources.network.F0RNetwork;
import v0id.f0resources.util.OreItem;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.StreamSupport;

public class TileDrill extends TileEntity implements ITickable
{
    public static final IForgeRegistry<Block> BLOCK_REGISTRY = GameRegistry.findRegistry(Block.class);

    public BlockPos centerPos = BlockPos.ORIGIN;
    public boolean isCenter = false;
    public EnergyStorage energyStorage = new EnergyStorage(F0RConfig.drillEnergy);
    public ItemStackHandler inventory = new ItemStackHandler(1)
    {
        @Override
        protected void onContentsChanged(int slot)
        {
            if (!TileDrill.this.world.isRemote)
            {
                F0RNetwork.sendDrillItem(TileDrill.this);
            }
        }
    };

    public BlockPos outputPos = BlockPos.ORIGIN;
    public boolean isRotating;
    public float progress;

    @Override
    public void update()
    {
        if (this.isCenter)
        {
            if (!this.inventory.getStackInSlot(0).isEmpty() && this.inventory.getStackInSlot(0).getItem() instanceof ItemDrillHead)
            {
                if (this.world.isRemote)
                {
                    if (this.isRotating)
                    {
                        this.world.spawnParticle(EnumParticleTypes.CRIT, this.pos.getX() + 0.5F + this.world.rand.nextDouble() - this.world.rand.nextDouble(), this.pos.getY(), this.pos.getZ() + 0.5F + this.world.rand.nextDouble() - this.world.rand.nextDouble(), this.world.rand.nextDouble() - this.world.rand.nextDouble(), 0.5F, this.world.rand.nextDouble() - this.world.rand.nextDouble());
                    }
                }
                else
                {
                    if (this.checkBase())
                    {
                        if (this.energyStorage.extractEnergy(F0RConfig.drillEnergyConsumption, true) >= F0RConfig.drillEnergyConsumption)
                        {
                            this.energyStorage.extractEnergy(F0RConfig.drillEnergyConsumption, false);
                            this.setRotating(true);
                            if (this.outputPos != BlockPos.ORIGIN)
                            {
                                this.progress += ((ItemDrillHead) this.inventory.getStackInSlot(0).getItem()).material.speed;
                                if (this.progress >= F0RConfig.drillRequiredProgress)
                                {
                                    this.progress -= F0RConfig.drillRequiredProgress;
                                    this.tryProduceResource(((ItemDrillHead) this.inventory.getStackInSlot(0).getItem()).material);
                                }
                            }
                        }
                        else
                        {
                            this.setRotating(false);
                        }
                    }
                    else
                    {
                        this.setRotating(false);
                    }
                }
            }
            else
            {
                if (!this.world.isRemote)
                {
                    this.setRotating(false);
                }
            }
        }
    }

    public boolean checkBase()
    {
        Block block = BLOCK_REGISTRY.getValue(new ResourceLocation(F0RConfig.requiredBlock));
        for (int dx = -1; dx <= 1; ++dx)
        {
            for (int dz = -1; dz <= 1; ++dz)
            {
                if (!this.world.getBlockState(this.pos.add(dx, -1, dz)).getBlock().isAssociatedBlock(block))
                {
                    return false;
                }
            }
        }

        return true;
    }

    public void tryProduceResource(DrillMaterialEntry entry)
    {
        ChunkPos cpos = new ChunkPos(this.getPos());
        IF0RWorld if0RWorld = IF0RWorld.of(this.world);
        IChunkData data = if0RWorld.getLoadedChunkData(cpos);
        if (data != null)
        {
            if (data.getSize() > 0)
            {
                List<OreItem> weightedList = Lists.newArrayList();
                StreamSupport.stream(data.spliterator(), false).forEach(e -> weightedList.add(new OreItem(e)));
                OreItem oreItem = WeightedRandom.getRandomItem(this.world.rand, weightedList);
                IOreData oreData = oreItem.data;
                while (oreData.getTierReq() > entry.tier)
                {
                    weightedList.remove(oreItem);
                    if (weightedList.size() == 0)
                    {
                        return;
                    }
                    else
                    {
                        if (weightedList.size() == 1)
                        {
                            oreItem = weightedList.get(0);
                        }
                        else
                        {
                            oreItem = WeightedRandom.getRandomItem(this.world.rand, weightedList);
                        }

                        oreData = oreItem.data;
                    }
                }

                ItemStack result = oreData.createOreItem(F0RConfig.oreResultSize);
                TileEntity tile = this.world.getTileEntity(this.outputPos);
                EnumFacing facing = EnumFacing.getFacingFromVector(this.pos.getX() - this.centerPos.getX(), 0, this.pos.getZ() - this.centerPos.getZ());
                boolean inserted = true;
                if (tile != null && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing))
                {
                    IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing);
                    if(!ItemHandlerHelper.insertItem(handler, result, true).isEmpty())
                    {
                        inserted = false;
                    }
                    else
                    {
                        ItemHandlerHelper.insertItem(handler, result, false);
                    }
                }
                else
                {
                    if (tile == null)
                    {
                        BlockPos at = BlockPos.ORIGIN;
                        if (this.world.isAirBlock(this.outputPos))
                        {
                            at = this.outputPos;
                        }
                        else
                        {
                            at = this.outputPos.up();
                        }

                        if (this.world.isAirBlock(at) && at != BlockPos.ORIGIN)
                        {
                            InventoryHelper.spawnItemStack(this.world, at.getX(), at.getY(), at.getZ(), result);
                        }
                        else
                        {
                            inserted = false;
                        }
                    }
                }

                if (inserted)
                {
                    if (F0RConfig.reduceOreInTheChunk)
                    {
                        oreData.setOreAmount(oreData.getOreAmount() - F0RConfig.oreReducedBy);
                        if (oreData.getOreAmount() == 0)
                        {
                            data.removeOreData(oreData);
                        }
                    }

                    if (this.inventory.getStackInSlot(0).getMaxDamage() != 0)
                    {
                        this.inventory.getStackInSlot(0).setItemDamage(this.inventory.getStackInSlot(0).getItemDamage() + F0RConfig.drillHeadDamage);
                        if (this.inventory.getStackInSlot(0).getItemDamage() >= this.inventory.getStackInSlot(0).getMaxDamage())
                        {
                            this.inventory.setStackInSlot(0, ItemStack.EMPTY);
                        }
                    }
                }
            }
        }
        else
        {
            F0Resources.modLogger.warn("Unable to get chunk data at {}!", cpos.toString());
        }
    }

    public void setRotating(boolean b)
    {
        if (this.isRotating != b)
        {
            this.isRotating = b;
            F0RNetwork.sendDrillRotating(this);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        this.centerPos = NBTUtil.getPosFromTag(compound.getCompoundTag("centerPos"));
        this.outputPos = NBTUtil.getPosFromTag(compound.getCompoundTag("outputPos"));
        this.isCenter = compound.getBoolean("isCenter");
        this.energyStorage.receiveEnergy(compound.getInteger("energy"), false);
        this.inventory.deserializeNBT(compound.getCompoundTag("inventory"));
        this.isRotating = compound.getBoolean("rotating");
        this.progress = compound.getFloat("progress");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        NBTTagCompound ret = super.writeToNBT(compound);
        ret.setTag("centerPos", NBTUtil.createPosTag(this.centerPos));
        ret.setTag("outputPos", NBTUtil.createPosTag(this.outputPos));
        ret.setBoolean("isCenter", this.isCenter);
        ret.setInteger("energy", this.energyStorage.getEnergyStored());
        ret.setTag("inventory", this.inventory.serializeNBT());
        ret.setBoolean("rotating", this.isRotating);
        ret.setFloat("progress", this.progress);
        return ret;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return (capability == CapabilityEnergy.ENERGY && this.centerPos != BlockPos.ORIGIN) || (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && this.centerPos != BlockPos.ORIGIN) || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityEnergy.ENERGY && this.centerPos != BlockPos.ORIGIN ? this.isCenter ? CapabilityEnergy.ENERGY.cast(this.energyStorage) : this.world.getTileEntity(this.centerPos).getCapability(capability, facing) : capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && this.centerPos != BlockPos.ORIGIN ? this.isCenter ? CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.inventory) : this.world.getTileEntity(this.centerPos).getCapability(capability, facing) : super.getCapability(capability, facing);
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(this.getPos(), 0, this.serializeNBT());
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        return this.serializeNBT();
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        this.deserializeNBT(pkt.getNbtCompound());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag)
    {
        this.deserializeNBT(tag);
    }

    @Override
    public boolean hasFastRenderer()
    {
        return true;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        return new AxisAlignedBB(this.pos.getX() - 1, this.pos.getY(), this.pos.getZ() - 1, this.pos.getX() + 2, this.pos.getY() + 1, this.pos.getZ() + 2);
    }
}
