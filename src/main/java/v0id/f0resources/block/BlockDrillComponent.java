package v0id.f0resources.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import v0id.api.f0resources.data.F0RBlocks;
import v0id.api.f0resources.data.F0RCreativeTabs;
import v0id.api.f0resources.data.F0RRegistryNames;
import v0id.f0resources.network.F0RNetwork;
import v0id.f0resources.tile.TileDrill;

public class BlockDrillComponent extends Block
{
    public BlockDrillComponent()
    {
        super(Material.IRON);
        this.setRegistryName(F0RRegistryNames.asLocation(F0RRegistryNames.drillComponent));
        this.setUnlocalizedName(this.getRegistryName().toString().replace(':', '.'));
        this.setHardness(3);
        this.setResistance(10);
        this.setCreativeTab(F0RCreativeTabs.tabF0R);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        BlockPos center = BlockPos.ORIGIN;
        if (isComponent(worldIn, pos.north()) && isComponent(worldIn, pos.south()) && isComponent(worldIn, pos.east()) && isComponent(worldIn, pos.west()))
        {
            center = pos;
        }
        else
        {
            if (isComponent(worldIn, pos.north()) && isComponent(worldIn, pos.south()))
            {
                if (isComponent(worldIn, pos.east()))
                {
                    center = pos.east();
                }
                else
                {
                    center = pos.west();
                }
            }
            else
            {
                if (isComponent(worldIn, pos.east()) && isComponent(worldIn, pos.west()))
                {
                    if (isComponent(worldIn, pos.north()))
                    {
                        center = pos.north();
                    }
                    else
                    {
                        center = pos.south();
                    }
                }
                else
                {
                    if (isComponent(worldIn, pos.north()) && isComponent(worldIn, pos.west()))
                    {
                        center = pos.north().west();
                    }
                    else
                    {
                        if (isComponent(worldIn, pos.north()) && isComponent(worldIn, pos.east()))
                        {
                            center = pos.north().east();
                        }
                        else
                        {
                            if (isComponent(worldIn, pos.south()) && isComponent(worldIn, pos.west()))
                            {
                                center = pos.south().west();
                            }
                            else
                            {
                                if (isComponent(worldIn, pos.south()) && isComponent(worldIn, pos.east()))
                                {
                                    center = pos.south().east();
                                }
                            }
                        }
                    }
                }
            }
        }

        if (center != BlockPos.ORIGIN)
        {
            for (int dx = -1; dx <= 1; ++dx)
            {
                for (int dz = -1; dz <= 1; ++dz)
                {
                    BlockPos at = center.add(dx, 0, dz);
                    if (!isComponent(worldIn, at))
                    {
                        return;
                    }
                }
            }

            for (int dx = -1; dx <= 1; ++dx)
            {
                for (int dz = -1; dz <= 1; ++dz)
                {
                    BlockPos at = center.add(dx, 0, dz);
                    worldIn.setBlockState(at, F0RBlocks.drillPart.getDefaultState());
                    TileDrill tile = (TileDrill) worldIn.getTileEntity(at);
                    tile.centerPos = center;
                    if (dx == 0 && dz == 0)
                    {
                        tile.isCenter = true;
                        F0RNetwork.sendDrillCenter(tile);
                    }
                }
            }
        }
    }

    private static boolean isComponent(World world, BlockPos pos)
    {
        return world.getBlockState(pos).getBlock() == F0RBlocks.drillComponent;
    }
}
