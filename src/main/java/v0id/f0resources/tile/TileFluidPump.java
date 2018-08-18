package v0id.f0resources.tile;

import joptsimple.internal.Strings;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.commons.lang3.ArrayUtils;
import v0id.api.f0resources.world.IChunkData;
import v0id.api.f0resources.world.IF0RWorld;
import v0id.api.f0resources.world.IFluidData;
import v0id.f0resources.config.F0RConfig;
import v0id.f0resources.config.FluidEntry;
import v0id.f0resources.network.F0RNetwork;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

public class TileFluidPump extends TileMultiblock implements ITickable, IAnimated
{
    public EnergyStorage energyStorage = new EnergyStorage(F0RConfig.pumpEnergy);
    public FluidTank fluidTank = new FluidTank(F0RConfig.pumpTankStorage)
    {
        @Override
        protected void onContentsChanged()
        {
            if (!TileFluidPump.this.world.isRemote)
            {
                F0RNetwork.sendFluidTank(TileFluidPump.this, this.fluid);
            }
        }
    };

    public ItemStackHandler inventory = new ItemStackHandler(1);
    public boolean isRotating;

    @Override
    public void update()
    {
        if (this.isCenter && !this.world.isRemote)
        {
            if (this.checkBase())
            {
                Fluid allowedFluid = null;
                if (this.fluidTank.getFluid() != null)
                {
                    allowedFluid = this.fluidTank.getFluid().getFluid();
                }
                else
                {
                    if (!this.inventory.getStackInSlot(0).isEmpty())
                    {
                        IFluidHandlerItem fluidHandlerItem = this.inventory.getStackInSlot(0).getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
                        if (fluidHandlerItem != null)
                        {
                            allowedFluid = fluidHandlerItem.getTankProperties().length > 0 && fluidHandlerItem.getTankProperties()[0].getContents() != null ? fluidHandlerItem.getTankProperties()[0].getContents().getFluid() : null;
                        }
                    }
                }

                if (this.fluidTank.getFluid() == null || this.fluidTank.getFluid().getFluid() == allowedFluid)
                {
                    if (this.consumePower(true))
                    {
                        this.consumePower(false);
                        this.setRotating(true);
                        IF0RWorld world = IF0RWorld.of(this.world);
                        IChunkData data = world.getLoadedChunkData(new ChunkPos(this.getPos()));
                        if (data != null && data.getFluidLength() > 0)
                        {
                            IFluidData fluidData = null;
                            if (allowedFluid != null)
                            {
                                Iterator<IFluidData> iter = data.fluidIterator();
                                while (iter.hasNext())
                                {
                                    fluidData = iter.next();
                                    if (fluidData.getFluid() == allowedFluid)
                                    {
                                        break;
                                    }
                                }
                            }
                            else
                            {
                                fluidData = data.getFluidData(this.world.rand.nextInt(data.getFluidLength()));
                            }

                            if (fluidData != null && (fluidData.getFluid() == allowedFluid || allowedFluid == null))
                            {
                                FluidEntry entry = FluidEntry.findByFluid(fluidData.getFluid());
                                int fluidDrained = 10;
                                if (entry != null)
                                {
                                    if (entry.isDrainRateBasedOnFluidAmount)
                                    {
                                        float val = (float) ((double) fluidData.getFluidAmount() / fluidData.getGeneratedAmount());
                                        fluidDrained = (int) (entry.fluidDrainRate * (entry.fluidMinimum + (entry.fluidMaximum - entry.fluidMinimum) * val));
                                    }
                                    else
                                    {
                                        fluidDrained = entry.fluidDrainRate;
                                    }
                                }

                                fluidDrained = (int) Math.min(Math.min(fluidDrained, fluidData.getFluidAmount()), this.fluidTank.getFluid() == null ? this.fluidTank.getCapacity() : this.fluidTank.getCapacity() - this.fluidTank.getFluid().amount);
                                if (fluidDrained > 0)
                                {
                                    this.fluidTank.fill(fluidData.createFluidStack(fluidDrained), true);
                                    fluidData.setFluidAmount(fluidData.getFluidAmount() - fluidDrained);
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
            else
            {
                this.setRotating(false);
            }

        }
    }

    public boolean checkBase()
    {
        Block[] block = Arrays.stream(F0RConfig.requiredBlocks).filter(s -> !Strings.isNullOrEmpty(s)).map(ResourceLocation::new).map(TileDrill.BLOCK_REGISTRY::getValue).filter(Objects::nonNull).toArray(Block[]::new);
        for (int dx = -1; dx <= 1; ++dx)
        {
            for (int dz = -1; dz <= 1; ++dz)
            {
                if (!ArrayUtils.contains(block, this.world.getBlockState(this.pos.add(dx, -1, dz)).getBlock()))
                {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean consumePower(boolean simulate)
    {
        return this.energyStorage.extractEnergy(F0RConfig.drillEnergyConsumption, simulate) >= F0RConfig.drillEnergyConsumption;
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

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        this.energyStorage.extractEnergy(Integer.MAX_VALUE, false);
        this.energyStorage.receiveEnergy(compound.getInteger("energy"), false);
        this.fluidTank.readFromNBT(compound.getCompoundTag("tank"));
        this.inventory.deserializeNBT(compound.getCompoundTag("inventory"));
        this.centerPos = NBTUtil.getPosFromTag(compound.getCompoundTag("centerPos"));
        this.isCenter = compound.getBoolean("isCenter");
        this.isRotating = compound.getBoolean("rotating");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        NBTTagCompound ret = super.writeToNBT(compound);
        ret.setInteger("energy", this.energyStorage.getEnergyStored());
        ret.setTag("tank", this.fluidTank.writeToNBT(new NBTTagCompound()));
        ret.setTag("inventory", this.inventory.serializeNBT());
        ret.setTag("centerPos", NBTUtil.createPosTag(this.centerPos));
        ret.setBoolean("isCenter", this.isCenter);
        ret.setBoolean("rotating", this.isRotating);
        return ret;
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
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return (capability == CapabilityEnergy.ENERGY && this.centerPos != BlockPos.ORIGIN) || (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && this.centerPos != BlockPos.ORIGIN) || (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && this.centerPos != BlockPos.ORIGIN) || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityEnergy.ENERGY && this.centerPos != BlockPos.ORIGIN ? this.isCenter ? CapabilityEnergy.ENERGY.cast(this.energyStorage) : this.world.getTileEntity(this.centerPos).getCapability(capability, facing) : capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && this.centerPos != BlockPos.ORIGIN ? this.isCenter ? CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this.fluidTank) : this.world.getTileEntity(this.centerPos).getCapability(capability, facing) : capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && this.centerPos != BlockPos.ORIGIN ? this.isCenter ? CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.inventory) : this.world.getTileEntity(this.centerPos).getCapability(capability, facing) : super.getCapability(capability, facing);
    }

    @Override
    public boolean hasFastRenderer()
    {
        return F0RConfig.useFastTESR;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        return new AxisAlignedBB(this.pos.getX() - 1, this.pos.getY(), this.pos.getZ() - 1, this.pos.getX() + 2, this.pos.getY() + 4, this.pos.getZ() + 2);
    }
}
