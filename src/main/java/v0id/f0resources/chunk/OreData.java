package v0id.f0resources.chunk;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.registry.GameRegistry;
import v0id.api.f0resources.world.IOreData;

public class OreData implements INBTSerializable<NBTTagCompound>, IOreData
{
    public int amount;
    public Item oreBlock;
    public short oreMeta;
    public byte tierReq;

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound ret = new NBTTagCompound();
        ret.setInteger("amount", this.amount);
        ret.setString("oreBlock", this.oreBlock.getRegistryName().toString());
        ret.setShort("oreMeta", this.oreMeta);
        ret.setByte("tierReq", this.tierReq);
        return ret;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        this.amount = nbt.getInteger("amount");
        this.oreBlock = GameRegistry.findRegistry(Item.class).getValue(new ResourceLocation(nbt.getString("oreBlock")));
        this.oreMeta = nbt.getShort("oreMeta");
        this.tierReq = nbt.getByte("tierReq");
    }

    @Override
    public ItemStack createOreItem(int amount)
    {
        return new ItemStack(this.getOreItem(), amount, this.getOreMeta());
    }

    @Override
    public Item getOreItem()
    {
        return this.oreBlock;
    }

    @Override
    public void setOreItem(Item item)
    {
        this.oreBlock = item;
    }

    @Override
    public short getOreMeta()
    {
        return this.oreMeta;
    }

    @Override
    public void setOreMeta(short s)
    {
        this.oreMeta = s;
    }

    @Override
    public int getOreAmount()
    {
        return this.amount;
    }

    @Override
    public void setOreAmount(int i)
    {
        this.amount = i;
    }

    @Override
    public int getTierReq()
    {
        return this.tierReq;
    }

    @Override
    public void setTierReq(int i)
    {
        this.tierReq = (byte) i;
    }
}
