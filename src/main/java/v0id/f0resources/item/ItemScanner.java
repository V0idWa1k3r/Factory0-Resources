package v0id.f0resources.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import v0id.api.f0resources.data.F0RCreativeTabs;
import v0id.api.f0resources.data.F0RRegistryNames;
import v0id.f0resources.config.F0RConfig;
import v0id.f0resources.network.F0RNetwork;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemScanner extends Item
{
    private final boolean isAdvanced;

    public ItemScanner(boolean advanced)
    {
        super();
        this.isAdvanced = advanced;
        this.setRegistryName(advanced ? F0RRegistryNames.asLocation(F0RRegistryNames.advancedScanner) : F0RRegistryNames.asLocation(F0RRegistryNames.scanner));
        this.setUnlocalizedName(this.getRegistryName().toString().replace(':', '.'));
        this.setMaxDamage(100);
        this.setMaxStackSize(1);
        this.setCreativeTab(F0RCreativeTabs.tabF0R);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        ItemStack is = playerIn.getHeldItem(handIn);
        if (is.getItemDamage() == 0)
        {
            if (!worldIn.isRemote)
            {
                IEnergyStorage storage = is.getCapability(CapabilityEnergy.ENERGY, null);
                if (storage.extractEnergy(F0RConfig.scannerEnergyCost, false) >= F0RConfig.scannerEnergyCost)
                {
                    is.setItemDamage(99);
                    F0RNetwork.sendScan(playerIn, this.isAdvanced);
                    worldIn.playSound(null, playerIn.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1, 0.1F);
                }
            }

            return new ActionResult<>(EnumActionResult.SUCCESS, is);
        }
        else
        {
            return new ActionResult<>(EnumActionResult.PASS, is);
        }
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
    {
        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
        if (stack.getItemDamage() > 0)
        {
            stack.setItemDamage(stack.getItemDamage() - 1);
        }
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (tab == this.getCreativeTab())
        {
            super.getSubItems(tab, items);
            ItemStack is = new ItemStack(this, 1, 0);
            is.getCapability(CapabilityEnergy.ENERGY, null).receiveEnergy(Integer.MAX_VALUE, false);
            items.add(is);
        }
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        return new ICapabilitySerializable<NBTTagInt>()
        {
            private EnergyStorage energyStorage = new EnergyStorage(F0RConfig.scannerMaxEnergy)
            {
                @Override
                public int getEnergyStored()
                {
                    return stack.hasTagCompound() ? stack.getTagCompound().getInteger("f0r:energy") : 0;
                }

                public void setEnergyStored(int value)
                {
                    if (!stack.hasTagCompound())
                    {
                        stack.setTagCompound(new NBTTagCompound());
                    }

                    stack.getTagCompound().setInteger("f0r:energy", value);
                }

                @Override
                public int receiveEnergy(int maxReceive, boolean simulate)
                {
                    if (!this.canReceive())
                    {
                        return 0;
                    }

                    int energyReceived = Math.min(capacity - this.getEnergyStored(), Math.min(this.maxReceive, maxReceive));
                    if (!simulate)
                    {
                        this.setEnergyStored(this.getEnergyStored() + energyReceived);
                    }

                    return energyReceived;
                }

                @Override
                public int extractEnergy(int maxExtract, boolean simulate)
                {
                    if (!canExtract())
                    {
                        return 0;
                    }

                    int energyExtracted = Math.min(this.getEnergyStored(), Math.min(this.maxExtract, maxExtract));
                    if (!simulate)
                    {
                        this.setEnergyStored(this.getEnergyStored() - energyExtracted);
                    }

                    return energyExtracted;
                }
            };

            @Override
            public NBTTagInt serializeNBT()
            {
                return new NBTTagInt(this.energyStorage.getEnergyStored());
            }

            @Override
            public void deserializeNBT(NBTTagInt nbt)
            {
                this.energyStorage.extractEnergy(Integer.MAX_VALUE, false);
                this.energyStorage.receiveEnergy(nbt.getInt(), false);
            }

            @Override
            public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
            {
                return capability == CapabilityEnergy.ENERGY;
            }

            @Nullable
            @Override
            public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
            {
                return capability == CapabilityEnergy.ENERGY ? CapabilityEnergy.ENERGY.cast(this.energyStorage) : null;
            }
        };
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null);
        tooltip.add(I18n.format("txt.f0r.rfStored", storage.getEnergyStored(), storage.getMaxEnergyStored()));
    }
}
