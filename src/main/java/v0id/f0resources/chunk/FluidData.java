package v0id.f0resources.chunk;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import v0id.api.f0resources.world.IFluidData;

public class FluidData implements IFluidData, INBTSerializable<NBTTagCompound>
{
    public Fluid fluid;
    public long amount;
    public long generatedAmount;

    @Override
    public FluidStack createFluidStack(int fluidAmount)
    {
        return new FluidStack(this.fluid, fluidAmount);
    }

    @Override
    public Fluid getFluid()
    {
        return this.fluid;
    }

    @Override
    public long getFluidAmount()
    {
        return this.amount;
    }

    @Override
    public long getGeneratedAmount()
    {
        return this.generatedAmount;
    }

    @Override
    public void setFluid(Fluid f)
    {
        this.fluid = f;
    }

    @Override
    public void setFluidAmount(long i)
    {
        this.amount = i;
        if (this.amount > this.generatedAmount)
        {
            this.generatedAmount = i;
        }
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound ret = new NBTTagCompound();
        ret.setString("fluid", this.fluid.getName());
        ret.setLong("amount", this.amount);
        ret.setLong("gen", this.generatedAmount);
        return ret;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        this.fluid = FluidRegistry.getFluid(nbt.getString("fluid"));
        this.amount = nbt.getLong("amount");
        this.generatedAmount = nbt.getLong("gen");
    }
}
