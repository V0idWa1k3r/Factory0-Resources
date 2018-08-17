package v0id.f0resources.tile;

import joptsimple.internal.Strings;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.commons.lang3.ArrayUtils;
import v0id.api.f0resources.capability.CapabilityFuelHandler;
import v0id.f0resources.capability.FuelHandler;
import v0id.f0resources.config.F0RConfig;
import v0id.f0resources.network.F0RNetwork;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Objects;

public class TileBurnerDrill extends AbstractDrill implements ITickable
{
    public static final IForgeRegistry<Block> BLOCK_REGISTRY = GameRegistry.findRegistry(Block.class);
    public ItemStackHandler inventory = new ItemStackHandler(2)
    {
        @Override
        protected void onContentsChanged(int slot)
        {
            if (!TileBurnerDrill.this.world.isRemote && slot == 0)
            {
                F0RNetwork.sendDrillItem(TileBurnerDrill.this);
            }
        }
    };

    public FuelHandler fuelHandler = new FuelHandler();
    public boolean hasFuel;

    @Override
    public boolean checkBase()
    {
        Block[] block = Arrays.stream(F0RConfig.requiredBlocks).filter(s -> !Strings.isNullOrEmpty(s)).map(ResourceLocation::new).map(BLOCK_REGISTRY::getValue).filter(Objects::nonNull).toArray(Block[]::new);
        return ArrayUtils.contains(block, this.world.getBlockState(this.pos.down()).getBlock()) && ArrayUtils.contains(block,this.world.getBlockState(this.pos.down().south()).getBlock()) && ArrayUtils.contains(block,this.world.getBlockState(this.pos.down().east()).getBlock()) && ArrayUtils.contains(block,this.world.getBlockState(this.pos.down().south().east()).getBlock());
    }

    @Override
    public boolean consumePower(boolean simulate)
    {
        if (simulate)
        {
            boolean ret = this.fuelHandler.consumeFuel();
            if (!ret)
            {
                ItemStack is = this.inventory.getStackInSlot(1);
                if (!is.isEmpty())
                {
                    int i = TileEntityFurnace.getItemBurnTime(is);
                    if (i > 0)
                    {
                        ret = true;
                        this.setHasFuel(true);
                        this.fuelHandler.setTotalFuel(i);
                        is.shrink(1);
                    }
                    else
                    {
                        this.setHasFuel(false);
                    }
                }
                else
                {
                    this.setHasFuel(false);
                }
            }

            return ret;
        }
        else
        {
            return true;
        }
    }

    public void setHasFuel(boolean b)
    {
        boolean b1 = this.hasFuel;
        this.hasFuel = b;
        if (b != b1)
        {
            F0RNetwork.sendDrillFuel(this);
        }
    }

    @Override
    public void update()
    {
        super.update();
        if (this.isCenter && this.world.isRemote && this.hasFuel)
        {
            EnumFacing facing = EnumFacing.getHorizontal(this.world.rand.nextInt(4));
            this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.pos.getX() + 1F + facing.getFrontOffsetX() / 3F, this.pos.getY() + 1.5F, this.pos.getZ() + 1F + facing.getFrontOffsetZ() / 3F, 0, 0.1F, 0);
        }
    }

    @Override
    public void spawnWorkingParticles()
    {
        this.world.spawnParticle(EnumParticleTypes.CRIT, this.pos.getX() + 1F, this.pos.getY(), this.pos.getZ() + 1F, this.world.rand.nextFloat() - this.world.rand.nextFloat(), this.world.rand.nextFloat(), this.world.rand.nextFloat() - this.world.rand.nextFloat());
    }

    @Override
    public float getRequiredProgress()
    {
        return super.getRequiredProgress() * 1.5F;
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
        this.inventory.deserializeNBT(compound.getCompoundTag("inventory"));
        this.fuelHandler.deserializeNBT(compound.getCompoundTag("fuelHandler"));
        this.hasFuel = compound.getBoolean("hasFuel");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        NBTTagCompound ret = super.writeToNBT(compound);
        ret.setTag("inventory", this.inventory.serializeNBT());
        ret.setTag("fuelHandler", this.fuelHandler.serializeNBT());
        ret.setBoolean("hasFuel", this.hasFuel);
        return ret;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return (capability == CapabilityFuelHandler.FUEL_HANDLER_CAPABILITY && this.centerPos != BlockPos.ORIGIN) || (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && this.centerPos != BlockPos.ORIGIN) || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityFuelHandler.FUEL_HANDLER_CAPABILITY && this.centerPos != BlockPos.ORIGIN ? this.isCenter ? CapabilityFuelHandler.FUEL_HANDLER_CAPABILITY.cast(this.fuelHandler) : this.world.getTileEntity(this.centerPos).getCapability(capability, facing) : capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && this.centerPos != BlockPos.ORIGIN ? this.isCenter ? CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.inventory) : this.world.getTileEntity(this.centerPos).getCapability(capability, facing) : super.getCapability(capability, facing);
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
        return new AxisAlignedBB(this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.pos.getX() + 2, this.pos.getY() + 1, this.pos.getZ() + 2);
    }
}
