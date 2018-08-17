package v0id.f0resources.tile;

import joptsimple.internal.Strings;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.commons.lang3.ArrayUtils;
import v0id.f0resources.config.F0RConfig;
import v0id.f0resources.network.F0RNetwork;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Objects;

public class TileDrill extends AbstractDrill implements ITickable
{
    public static final IForgeRegistry<Block> BLOCK_REGISTRY = GameRegistry.findRegistry(Block.class);
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

    @Override
    public boolean checkBase()
    {
        Block[] block = Arrays.stream(F0RConfig.requiredBlocks).filter(s -> !Strings.isNullOrEmpty(s)).map(ResourceLocation::new).map(BLOCK_REGISTRY::getValue).filter(Objects::nonNull).toArray(Block[]::new);
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

    @Override
    public boolean consumePower(boolean simulate)
    {
        return this.energyStorage.extractEnergy(F0RConfig.drillEnergyConsumption, simulate) >= F0RConfig.drillEnergyConsumption;
    }

    @Override
    public void spawnWorkingParticles()
    {
        this.world.spawnParticle(EnumParticleTypes.CRIT, this.pos.getX() + 0.5F + this.world.rand.nextDouble() - this.world.rand.nextDouble(), this.pos.getY(), this.pos.getZ() + 0.5F + this.world.rand.nextDouble() - this.world.rand.nextDouble(), this.world.rand.nextDouble() - this.world.rand.nextDouble(), 0.5F, this.world.rand.nextDouble() - this.world.rand.nextDouble());
    }

    @Override
    public ItemStack getDrillHead()
    {
        return this.inventory.getStackInSlot(0);
    }

    @Override
    public void setDrillHead(ItemStack is)
    {
        this.inventory.setStackInSlot(0, is);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        this.energyStorage.extractEnergy(Integer.MAX_VALUE, false);
        this.energyStorage.receiveEnergy(compound.getInteger("energy"), false);
        this.inventory.deserializeNBT(compound.getCompoundTag("inventory"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        NBTTagCompound ret = super.writeToNBT(compound);
        ret.setInteger("energy", this.energyStorage.getEnergyStored());
        ret.setTag("inventory", this.inventory.serializeNBT());
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
        return F0RConfig.useFastTESR;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        return new AxisAlignedBB(this.pos.getX() - 1, this.pos.getY(), this.pos.getZ() - 1, this.pos.getX() + 2, this.pos.getY() + 1, this.pos.getZ() + 2);
    }
}
