package v0id.api.f0resources.capability;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public interface IFuelHandler extends INBTSerializable<NBTTagCompound>
{
    int getFuel();

    int getMaxFuel();

    void setFuel(int value);

    void setMaxFuel(int value);

    boolean consumeFuel();

    void setTotalFuel(int value);
}
