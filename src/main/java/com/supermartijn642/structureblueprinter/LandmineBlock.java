package com.supermartijn642.structureblueprinter;

import com.supermartijn642.core.ToolType;
import com.supermartijn642.core.block.BaseBlock;
import com.supermartijn642.core.block.BlockShape;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Created 7/8/2021 by SuperMartijn642
 */
public class LandmineBlock extends BaseBlock {

    private static final BlockShape SHAPE = BlockShape.or(
        BlockShape.createBlockShape(3, 0, 3, 13, 0.75, 13),
        BlockShape.createBlockShape(5, 0.75, 5, 11, 1.25, 11)
    );

    public static final PropertyBool ON = PropertyBool.create("on");

    public final LandmineType type;

    public LandmineBlock(LandmineType type){
        super(type.getSuffix() + "_landmine", false, Properties.create(Material.IRON, EnumDyeColor.GRAY).harvestTool(ToolType.PICKAXE).hardnessAndResistance(0.5f));
        this.type = type;

        this.setCreativeTab(Landmines.GROUP);

        this.setDefaultState(this.getDefaultState().withProperty(ON, false));
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos){
        TileEntity entity = world.getTileEntity(pos);
        return entity instanceof LandmineTileEntity && !((LandmineTileEntity)entity).hasShape() ? BlockShape.empty().simplify() : SHAPE.simplify();
    }

    private static boolean canSurvive(World world, BlockPos pos){
        BlockFaceShape shape = world.getBlockState(pos.down()).getBlockFaceShape(world, pos.down(), EnumFacing.UP);
        return shape == BlockFaceShape.CENTER || shape == BlockFaceShape.CENTER_BIG || shape == BlockFaceShape.CENTER_SMALL ||
            shape == BlockFaceShape.MIDDLE_POLE || shape == BlockFaceShape.MIDDLE_POLE_THICK || shape == BlockFaceShape.MIDDLE_POLE_THIN ||
            shape == BlockFaceShape.SOLID;
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos){
        return world.getBlockState(pos).getBlock().isReplaceable(world, pos) && canSurvive(world, pos);
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state){
        if(state.getBlock() == this && !canSurvive(world, pos)){
            if(world.getBlockState(pos).getBlock() == this){
                this.dropBlockAsItem(world, pos, state, 0);
                world.setBlockToAir(pos);
            }
        }
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos){
        if(state.getBlock() == this && !world.isRemote && !canSurvive(world, pos)){
            this.dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
        }
    }

    @Override
    public boolean hasTileEntity(IBlockState state){
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state){
        return this.type.getTileEntity();
    }

    @Override
    protected BlockStateContainer createBlockState(){
        return new BlockStateContainer(this, ON);
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity){
        TileEntity tileEntity = world.getTileEntity(pos);
        if(tileEntity instanceof LandmineTileEntity)
            ((LandmineTileEntity)tileEntity).onEntityCollide(entity);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
        TileEntity tileEntity = world.getTileEntity(pos);
        if(tileEntity instanceof LandmineTileEntity)
            return ((LandmineTileEntity)tileEntity).onRightClick(player, hand);
        return super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state){
        TileEntity tileEntity = world.getTileEntity(pos);
        if(tileEntity instanceof LandmineTileEntity)
            InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), ((LandmineTileEntity)tileEntity).getStack());
        super.breakBlock(world, pos, state);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state){
        return EnumBlockRenderType.INVISIBLE;
    }

    public boolean isOpaqueCube(IBlockState state){
        return false;
    }

    public boolean isFullCube(IBlockState state){
        return false;
    }

    @Override
    public int getMetaFromState(IBlockState state){
        return state.getValue(ON) ? 1 : 0;
    }

    @Override
    public IBlockState getStateFromMeta(int meta){
        return this.getDefaultState().withProperty(ON, meta != 0);
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face){
        return BlockFaceShape.UNDEFINED;
    }
}
