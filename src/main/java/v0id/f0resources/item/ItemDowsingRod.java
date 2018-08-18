package v0id.f0resources.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import v0id.api.f0resources.data.F0RCreativeTabs;
import v0id.api.f0resources.data.F0RRegistryNames;
import v0id.api.f0resources.world.IChunkData;
import v0id.api.f0resources.world.IF0RWorld;
import v0id.api.f0resources.world.IFluidData;
import v0id.f0resources.network.F0RNetwork;

public class ItemDowsingRod extends Item
{
    public ItemDowsingRod()
    {
        super();
        this.setRegistryName(F0RRegistryNames.asLocation(F0RRegistryNames.dowsingRod));
        this.setUnlocalizedName(this.getRegistryName().toString().replace(':', '.'));
        this.setMaxDamage(800);
        this.setMaxStackSize(1);
        this.setCreativeTab(F0RCreativeTabs.tabF0R);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote)
        {
            return EnumActionResult.SUCCESS;
        }

        IF0RWorld world = IF0RWorld.of(worldIn);
        IChunkData chunkData = world.getLoadedChunkData(new ChunkPos(pos));
        if (chunkData == null || chunkData.getFluidLength() == 0)
        {
            F0RNetwork.sendDowsing(null, player);
        }
        else
        {
            IFluidData fluidData = chunkData.getFluidData(worldIn.rand.nextInt(chunkData.getFluidLength()));
            F0RNetwork.sendDowsing(fluidData, player);
        }

        player.getHeldItem(hand).damageItem(1, player);
        return EnumActionResult.SUCCESS;
    }
}
