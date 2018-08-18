package v0id.api.f0resources.world;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public interface IFluidData
{
    FluidStack createFluidStack(int fluidAmount);

    Fluid getFluid();

    long getFluidAmount();

    long getGeneratedAmount();

    void setFluid(Fluid f);

    void setFluidAmount(long i);
}
