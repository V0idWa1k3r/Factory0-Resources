package v0id.api.f0resources.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class CapabilityFuelHandler
{
    @CapabilityInject(IFuelHandler.class)
    public static final Capability<IFuelHandler> FUEL_HANDLER_CAPABILITY = null;
}
