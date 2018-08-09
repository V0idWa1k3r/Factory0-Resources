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
import v0id.api.f0resources.world.IOreData;
import v0id.f0resources.network.F0RNetwork;

public class ItemProspectorsPick extends Item
{
    private final boolean isAdvanced;

    public ItemProspectorsPick(boolean advanced)
    {
        super();
        this.isAdvanced = advanced;
        this.setRegistryName(advanced ? F0RRegistryNames.asLocation(F0RRegistryNames.advancedProspectorsPick) : F0RRegistryNames.asLocation(F0RRegistryNames.prospectorsPick));
        this.setUnlocalizedName(this.getRegistryName().toString().replace(':', '.'));
        this.setMaxDamage(advanced ? 1562 : 132);
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
        if (chunkData == null || chunkData.getSize() == 0 || (!this.isAdvanced && worldIn.rand.nextBoolean()))
        {
            F0RNetwork.sendProspect(null, player);
        }
        else
        {
            IOreData oreData = chunkData.getOreData(worldIn.rand.nextInt(chunkData.getSize()));
            F0RNetwork.sendProspect(oreData, player);
        }

        player.getHeldItem(hand).damageItem(1, player);
        return EnumActionResult.SUCCESS;
    }
}
