package v0id.f0resources.tile;

import com.google.common.collect.Lists;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import v0id.api.f0resources.world.IChunkData;
import v0id.api.f0resources.world.IF0RWorld;
import v0id.api.f0resources.world.IOreData;
import v0id.f0resources.F0Resources;
import v0id.f0resources.config.DrillMaterialEntry;
import v0id.f0resources.config.F0RConfig;
import v0id.f0resources.config.OreEntry;
import v0id.f0resources.item.ItemDrillHead;
import v0id.f0resources.network.F0RNetwork;
import v0id.f0resources.util.OreItem;

import java.util.List;
import java.util.stream.StreamSupport;

public abstract class AbstractDrill extends TileMultiblock implements ITickable, IAnimated
{
    public BlockPos outputPos = BlockPos.ORIGIN;
    public boolean isRotating;
    public float progress;
    public ItemStack minedOre = ItemStack.EMPTY;
    public float minedMultiplier = 1F;

    @Override
    public void update()
    {
        if (this.isCenter)
        {
            ItemStack head = this.getDrillHead();
            if (!head.isEmpty() && head.getItem() instanceof ItemDrillHead)
            {
                if (this.world.isRemote)
                {
                    if (this.isRotating)
                    {
                        this.spawnWorkingParticles();
                    }
                }
                else
                {
                    if (F0RConfig.drillsWorkAnywhere || this.checkBase())
                    {
                        if (this.consumePower(true))
                        {
                            this.consumePower(false);
                            if (this.outputPos != BlockPos.ORIGIN)
                            {
                                if (this.progress < 0.001F)
                                {
                                    this.tryCreateMinedOre(((ItemDrillHead) head.getItem()).material);
                                }

                                if (this.minedOre.isEmpty())
                                {
                                    this.setRotating(false);
                                }
                                else
                                {
                                    this.setRotating(true);
                                    this.progress += ((ItemDrillHead) head.getItem()).material.speed;
                                    if (this.progress >= this.getRequiredProgress())
                                    {
                                        this.progress -= this.getRequiredProgress();
                                        this.tryProduceResource(((ItemDrillHead) head.getItem()).material);
                                        this.tryCreateMinedOre(((ItemDrillHead) head.getItem()).material);
                                    }
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

    public void tryCreateMinedOre(DrillMaterialEntry entry)
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

                this.minedOre = oreData.createOreItem(F0RConfig.oreResultSize);
                OreEntry oreEntry = OreEntry.findByItem(this.minedOre.getItem(), this.minedOre.getMetadata());
                this.minedMultiplier = oreEntry == null ? 1 : oreEntry.progressMultiplier;
            }
        }
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
                IOreData oreData = StreamSupport.stream(data.spliterator(), false).filter(oreEntry -> oreEntry.getOreItem() == this.minedOre.getItem() && oreEntry.getOreMeta() == this.minedOre.getMetadata()).findFirst().orElse(null);
                if (oreData == null)
                {
                    return;
                }

                ItemStack result = this.minedOre.copy();
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

                this.minedOre = ItemStack.EMPTY;
                this.minedMultiplier = 1F;
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

                    ItemStack head = this.getDrillHead();
                    if (head.getMaxDamage() != 0)
                    {
                        head.setItemDamage(head.getItemDamage() + F0RConfig.drillHeadDamage);
                        if (head.getItemDamage() >= head.getMaxDamage())
                        {
                            this.setDrillHead(ItemStack.EMPTY);
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
            F0RNetwork.sendAnimationState(this);
        }
    }

    @Override
    public void setAnimated(boolean b)
    {
        this.setRotating(b);
    }

    @Override
    public boolean isAnimated()
    {
        return this.isRotating;
    }

    public abstract ItemStack getDrillHead();
    public abstract void setDrillHead(ItemStack is);
    public abstract boolean checkBase();
    public abstract boolean consumePower(boolean simulate);
    public abstract void spawnWorkingParticles();
    public float getRequiredProgress()
    {
        return F0RConfig.drillRequiredProgress * this.minedMultiplier;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        this.centerPos = NBTUtil.getPosFromTag(compound.getCompoundTag("centerPos"));
        this.outputPos = NBTUtil.getPosFromTag(compound.getCompoundTag("outputPos"));
        this.isCenter = compound.getBoolean("isCenter");
        this.isRotating = compound.getBoolean("rotating");
        this.progress = compound.getFloat("progress");
        this.minedMultiplier = compound.getFloat("minedMultiplier");
        this.minedOre = new ItemStack(compound.getCompoundTag("minedOre"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        NBTTagCompound ret = super.writeToNBT(compound);
        ret.setTag("centerPos", NBTUtil.createPosTag(this.centerPos));
        ret.setTag("outputPos", NBTUtil.createPosTag(this.outputPos));
        ret.setBoolean("isCenter", this.isCenter);
        ret.setBoolean("rotating", this.isRotating);
        ret.setFloat("progress", this.progress);
        ret.setFloat("minedMultiplier", this.minedMultiplier);
        ret.setTag("minedOre", this.minedOre.serializeNBT());
        return ret;
    }
}
