package v0id.api.f0resources.data;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class F0RCreativeTabs
{
    public static final CreativeTabs tabF0R = new CreativeTabs(F0RRegistryNames.MODID)
    {
        @Override
        public ItemStack getTabIconItem()
        {
            return new ItemStack(F0RItems.prospectorsPick);
        }
    };
}
