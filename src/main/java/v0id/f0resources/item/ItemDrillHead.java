package v0id.f0resources.item;

import com.google.common.collect.Lists;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import v0id.api.f0resources.data.F0RCreativeTabs;
import v0id.api.f0resources.data.F0RRegistryNames;
import v0id.f0resources.config.DrillMaterialEntry;

import java.util.List;

public class ItemDrillHead extends Item
{
    public final DrillMaterialEntry material;

    public static final List<ItemDrillHead> allDrillHeads = Lists.newArrayList();

    public ItemDrillHead(DrillMaterialEntry entry)
    {
        super();
        allDrillHeads.add(this);
        this.material = entry;
        this.setRegistryName(F0RRegistryNames.asLocation("item_drill_head." + entry.name.toLowerCase().replace(' ', '_')));
        this.setUnlocalizedName("f0-resources.item.drill_head");
        this.setCreativeTab(F0RCreativeTabs.tabF0R);
        this.setMaxStackSize(1);
        this.setMaxDamage(entry.durability);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return super.getUnlocalizedName(stack) + (this.material.isUnlocalized ? ('.' + this.material.name) : "");
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack)
    {
        return this.material.isUnlocalized ? super.getItemStackDisplayName(stack) : this.material.name;
    }
}
