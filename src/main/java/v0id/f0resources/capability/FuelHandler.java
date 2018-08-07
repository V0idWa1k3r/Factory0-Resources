package v0id.f0resources.capability;

import net.minecraft.nbt.NBTTagCompound;
import v0id.api.f0resources.capability.IFuelHandler;

public class FuelHandler implements IFuelHandler
{
    private int fuel = 0;
    private int maxFuel = 1;

    @Override
    public int getFuel()
    {
        return this.fuel;
    }

    @Override
    public int getMaxFuel()
    {
        return this.maxFuel;
    }

    @Override
    public void setFuel(int value)
    {
        this.fuel = Math.max(0, value);
    }

    @Override
    public void setMaxFuel(int value)
    {
        this.maxFuel = Math.max(1, value);
    }

    @Override
    public boolean consumeFuel()
    {
        boolean ret = this.fuel > 0;
        this.fuel = Math.max(0, --this.fuel);
        return ret;
    }

    @Override
    public void setTotalFuel(int value)
    {
        this.setFuel(value);
        this.setMaxFuel(value);
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound ret = new NBTTagCompound();
        ret.setInteger("fuel", this.fuel);
        ret.setInteger("maxFuel", this.maxFuel);
        return ret;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        this.fuel = nbt.getInteger("fuel");
        this.maxFuel = nbt.getInteger("maxFuel");
    }
}
