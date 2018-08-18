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
import v0id.f0resources.tile.TileBurnerDrill;

public class BlockBurnerDrillComponent extends Block
{
    public BlockBurnerDrillComponent()
    {
        super(Material.IRON);
        this.setRegistryName(F0RRegistryNames.asLocation(F0RRegistryNames.burnerDrillComponent));
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
        if (isComponent(worldIn, pos.east()) && isComponent(worldIn, pos.south()))
        {
            center = pos;
        }
        else
        {
            if (isComponent(worldIn, pos.east()) && isComponent(worldIn, pos.north()))
            {
                center = pos.north();
            }
            else
            {
                if (isComponent(worldIn, pos.west()) && isComponent(worldIn, pos.south()))
                {
                    center = pos.west();
                }
                else
                {
                    if (isComponent(worldIn, pos.west()) && isComponent(worldIn, pos.north()))
                    {
                        center = pos.west().north();
                    }
                }
            }
        }

        if (center != BlockPos.ORIGIN)
        {
            if (isComponent(worldIn, center.down()))
            {
                center = center.down();
            }
        }

        if (center != BlockPos.ORIGIN)
        {
            if (isComponent(worldIn, center) && isComponent(worldIn, center.south()) && isComponent(worldIn, center.east()) && isComponent(worldIn, center.east().south()) && isComponent(worldIn, center.up()) && isComponent(worldIn, center.up().south()) && isComponent(worldIn, center.up().east()) && isComponent(worldIn, center.up().east().south()))
            {
                for (int dx = 0; dx <= 1; ++dx)
                {
                    for (int dy = 0; dy <= 1; ++dy)
                    {
                        for (int dz = 0; dz <= 1; ++dz)
                        {
                            worldIn.setBlockState(center.add(dx, dy, dz), F0RBlocks.burnerDrillPart.getDefaultState());
                            TileBurnerDrill tbd = (TileBurnerDrill) worldIn.getTileEntity(center.add(dx, dy, dz));
                            tbd.centerPos = center;
                            if (dx == 0 && dz == 0 && dy == 0)
                            {
                                tbd.isCenter = true;
                                F0RNetwork.sendMultiblockCenter(tbd);
                            }
                        }
                    }
                }
            }
        }
    }

    private static boolean isComponent(World world, BlockPos pos)
    {
        return world.getBlockState(pos).getBlock() == F0RBlocks.burnerDrillComponent;
    }
}
