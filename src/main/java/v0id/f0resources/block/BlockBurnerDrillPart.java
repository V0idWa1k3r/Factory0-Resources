package v0id.f0resources.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import v0id.api.f0resources.data.F0RBlocks;
import v0id.api.f0resources.data.F0RRegistryNames;
import v0id.f0resources.F0Resources;
import v0id.f0resources.tile.TileBurnerDrill;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockBurnerDrillPart extends Block
{
    public BlockBurnerDrillPart()
    {
        super(Material.IRON);
        this.setRegistryName(F0RRegistryNames.asLocation(F0RRegistryNames.burnerDrillPart));
        this.setUnlocalizedName(this.getRegistryName().toString().replace(':', '.'));
        this.setHardness(3);
        this.setResistance(10);
        this.setCreativeTab(null);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileBurnerDrill();
    }

    private boolean isRecursiveBreaking;

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Item.getItemFromBlock(F0RBlocks.burnerDrillComponent);
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        return new ItemStack(F0RBlocks.burnerDrillComponent, 1, 0);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (!this.isRecursiveBreaking)
        {
            if (tile instanceof TileBurnerDrill)
            {
                if (((TileBurnerDrill) tile).centerPos != BlockPos.ORIGIN)
                {
                    BlockPos center = ((TileBurnerDrill) tile).centerPos;
                    this.isRecursiveBreaking = true;
                    for (int dx = 0; dx <= 1; ++dx)
                    {
                        for (int dy = 0; dy <= 1; ++dy)
                        {
                            for (int dz = 0; dz <= 1; ++dz)
                            {
                                BlockPos at = center.add(dx, dy, dz);
                                if (at.getX() != pos.getX() || at.getZ() != pos.getZ() || at.getY() != pos.getY())
                                {
                                    worldIn.setBlockState(at, F0RBlocks.burnerDrillComponent.getDefaultState());
                                }
                            }
                        }
                    }

                    this.isRecursiveBreaking = false;
                }
            }
        }

        if (tile instanceof TileBurnerDrill && ((TileBurnerDrill) tile).isCenter)
        {
            if (!((TileBurnerDrill) tile).inventory.getStackInSlot(0).isEmpty())
            {
                InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY() + 1, pos.getZ(), ((TileBurnerDrill) tile).inventory.getStackInSlot(0).copy());
            }

            if (!((TileBurnerDrill) tile).inventory.getStackInSlot(1).isEmpty())
            {
                InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY() + 1, pos.getZ(), ((TileBurnerDrill) tile).inventory.getStackInSlot(1).copy());
            }
        }

        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote)
        {
            return true;
        }

        if (playerIn.isSneaking())
        {
            TileBurnerDrill drill = (TileBurnerDrill) worldIn.getTileEntity(pos);
            if (drill.centerPos != BlockPos.ORIGIN)
            {
                TileEntity tile = worldIn.getTileEntity(drill.centerPos);
                if (tile instanceof TileBurnerDrill)
                {
                    BlockPos at = pos.offset(facing);
                    ((TileBurnerDrill) tile).outputPos = at;
                    playerIn.sendMessage(new TextComponentTranslation("txt.f0r.outputSet", at.getX(), at.getY(), at.getZ()));
                    return true;
                }
            }
        }
        else
        {
            TileBurnerDrill drill = (TileBurnerDrill) worldIn.getTileEntity(pos);
            if (drill.centerPos != BlockPos.ORIGIN)
            {
                TileEntity tile = worldIn.getTileEntity(drill.centerPos);
                if (tile instanceof TileBurnerDrill)
                {
                    playerIn.openGui(F0Resources.instance, 1, worldIn, tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ());
                    return true;
                }
            }
        }

        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }
}
