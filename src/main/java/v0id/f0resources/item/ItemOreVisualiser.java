package v0id.f0resources.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import v0id.api.f0resources.data.F0RCreativeTabs;
import v0id.api.f0resources.data.F0RRegistryNames;
import v0id.f0resources.F0Resources;
import v0id.f0resources.network.F0RNetwork;

public class ItemOreVisualiser extends Item
{
    public ItemOreVisualiser()
    {
        super();
        this.setRegistryName(F0RRegistryNames.asLocation(F0RRegistryNames.oreVisualizer));
        this.setUnlocalizedName(this.getRegistryName().toString().replace(':', '.'));
        this.setMaxStackSize(1);
        this.setCreativeTab(F0RCreativeTabs.tabF0R);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        if (!worldIn.isRemote)
        {
            F0RNetwork.sendSeed(playerIn);
            playerIn.openGui(F0Resources.instance, 2, worldIn, 0, 0, 0);
        }

        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
}
