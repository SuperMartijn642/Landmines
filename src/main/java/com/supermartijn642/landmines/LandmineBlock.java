package com.supermartijn642.landmines;

import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.block.BaseBlock;
import com.supermartijn642.core.block.BlockProperties;
import com.supermartijn642.core.block.BlockShape;
import com.supermartijn642.core.block.EntityHoldingBlock;
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
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * Created 7/8/2021 by SuperMartijn642
 */
public class LandmineBlock extends BaseBlock implements EntityHoldingBlock {

    private static final BlockShape SHAPE = BlockShape.or(
        BlockShape.createBlockShape(3, 0, 3, 13, 0.75, 13),
        BlockShape.createBlockShape(5, 0.75, 5, 11, 1.25, 11)
    );

    public static final PropertyBool ON = PropertyBool.create("on");

    public final LandmineType type;

    public LandmineBlock(LandmineType type){
        super(false, BlockProperties.create(Material.IRON, EnumDyeColor.GRAY).destroyTime(0.5f).explosionResistance(0.5f));
        this.type = type;

        this.setDefaultState(this.getDefaultState().withProperty(ON, false));
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess level, BlockPos pos){
        TileEntity entity = level.getTileEntity(pos);
        return entity instanceof LandmineBlockEntity && !((LandmineBlockEntity)entity).hasShape() ? BlockShape.empty().simplify() : SHAPE.simplify();
    }

    @Override
    public void neighborChanged(IBlockState state, World level, BlockPos pos, Block block, BlockPos fromPos){
        if(state.getBlock() == this && !level.isRemote && !canSurvive(level, pos)){
            this.dropBlockAsItem(level, pos, state, 0);
            level.setBlockToAir(pos);
        }
    }

    private static boolean canSurvive(World level, BlockPos pos){
        BlockFaceShape shape = level.getBlockState(pos.down()).getBlockFaceShape(level, pos.down(), EnumFacing.UP);
        return shape == BlockFaceShape.CENTER || shape == BlockFaceShape.CENTER_BIG || shape == BlockFaceShape.CENTER_SMALL ||
            shape == BlockFaceShape.MIDDLE_POLE || shape == BlockFaceShape.MIDDLE_POLE_THICK || shape == BlockFaceShape.MIDDLE_POLE_THIN ||
            shape == BlockFaceShape.SOLID;
    }

    @Override
    public boolean canPlaceBlockAt(World level, BlockPos pos){
        return level.getBlockState(pos).getBlock().isReplaceable(level, pos) && canSurvive(level, pos);
    }

    @Override
    public void onBlockAdded(World level, BlockPos pos, IBlockState state){
        if(state.getBlock() == this && !canSurvive(level, pos)){
            if(level.getBlockState(pos).getBlock() == this){
                this.dropBlockAsItem(level, pos, state, 0);
                level.setBlockToAir(pos);
            }
        }
    }

    @Override
    public TileEntity createNewBlockEntity(){
        return this.type.getBlockEntity();
    }

    @Override
    protected BlockStateContainer createBlockState(){
        return new BlockStateContainer(this, ON);
    }

    @Override
    public void onEntityCollidedWithBlock(World level, BlockPos pos, IBlockState state, Entity entity){
        TileEntity blockEntity = level.getTileEntity(pos);
        if(blockEntity instanceof LandmineBlockEntity)
            ((LandmineBlockEntity)blockEntity).onEntityCollide(entity);
    }

    @Override
    protected InteractionFeedback interact(IBlockState state, World level, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing hitSide, Vec3d hitLocation){
        TileEntity entity = level.getTileEntity(pos);
        if(entity instanceof LandmineBlockEntity)
            return ((LandmineBlockEntity)entity).onRightClick(player, hand) ? InteractionFeedback.SUCCESS : InteractionFeedback.PASS;
        return super.interact(state, level, pos, player, hand, hitSide, hitLocation);
    }

    @Override
    public void breakBlock(World level, BlockPos pos, IBlockState state){
        TileEntity entity = level.getTileEntity(pos);
        if(entity instanceof LandmineBlockEntity)
            InventoryHelper.spawnItemStack(level, pos.getX(), pos.getY(), pos.getZ(), ((LandmineBlockEntity)entity).getStack());

        super.breakBlock(level, pos, state);
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

    @Override
    protected void appendItemInformation(ItemStack stack, @Nullable IBlockAccess level, Consumer<ITextComponent> info, boolean advanced){
        info.accept(TextComponents.translation("landmines." + this.type.getSuffix() + ".info").color(TextFormatting.GRAY).get());
        if(this.type.itemFilter != null && this.type.tooltipItem != null)
            info.accept(TextComponents.translation("landmines.info.item", TextComponents.item(this.type.tooltipItem).color(TextFormatting.GOLD).get()).color(TextFormatting.GRAY).get());
        info.accept(TextComponents.translation("landmines.info.reusable", TextComponents.translation("landmines.info.reusable." + this.type.reusable.get()).color(TextFormatting.GOLD).get()).color(TextFormatting.GRAY).get());
    }
}
